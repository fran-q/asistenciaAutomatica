package com.appasistencia.websocket;

import com.appasistencia.services.FaceRecognitionService;
import com.appasistencia.services.FacialAttendanceService;
import com.appasistencia.services.JwtService;
import com.appasistencia.services.PlantillaBiometricaService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// WebSocket handler: procesa frames de camara para reconocimiento/enrolamiento facial
@Component
public class FaceWebSocketHandler extends AbstractWebSocketHandler {

    private static final Logger LOG = LoggerFactory.getLogger(FaceWebSocketHandler.class);

    private static final long COOLDOWN_SECONDS = 60;       // Cooldown entre registros del mismo usuario
    private static final long ENROLLMENT_INTERVAL_MS = 1000; // 1 muestra por segundo
    private static final int MAX_ENROLLMENT_SAMPLES = 20;    // Maximo de muestras por sesion de enrolamiento
    private static final long CACHE_TTL_SECONDS = 15;        // Refrescar embeddings cada 15 segundos

    // Dependencias
    private final FaceRecognitionService faceService;
    private final FacialAttendanceService attendanceService;
    private final PlantillaBiometricaService plantillaService;
    private final JwtService jwtService;

    // Estado por sesion WebSocket (un mapa por conexion activa)
    private final Map<String, SessionState> sessions = new ConcurrentHashMap<>();

    public FaceWebSocketHandler(FaceRecognitionService faceService,
                                 FacialAttendanceService attendanceService,
                                 PlantillaBiometricaService plantillaService,
                                 JwtService jwtService) {
        this.faceService = faceService;
        this.attendanceService = attendanceService;
        this.plantillaService = plantillaService;
        this.jwtService = jwtService;
    }

    // Conexion establecida: valida token JWT y carga embeddings de la institucion
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String query = session.getUri() != null ? session.getUri().getQuery() : null;
        String token = extractToken(query);

        if (token == null || !jwtService.isTokenValid(token)) {
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }

        Long idInstitucion = jwtService.extractIdInstitucion(token);
        String nombreUsuario = jwtService.extractNombreCompleto(token);

        SessionState state = new SessionState();
        state.idInstitucion = idInstitucion;
        state.nombreUsuario = nombreUsuario;
        state.mode = "RECOGNITION";
        state.embeddingsCache = plantillaService.getEmbeddingsByInstitucion(idInstitucion);
        state.cacheLoadedAt = Instant.now();

        sessions.put(session.getId(), state);
        LOG.info(">>> WS conectado: usuario={}, institucion={}, embeddings={}", nombreUsuario, idInstitucion, state.embeddingsCache.size());
    }

    // Mensaje de texto: cambia modo (RECOGNITION/ENROLLMENT) y setea usuario a enrolar
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        SessionState state = sessions.get(session.getId());
        if (state == null) return;

        String payload = message.getPayload();

        // Simple JSON parsing without Jackson
        String mode = extractJsonString(payload, "mode");
        if (mode != null) {
            state.mode = mode;
        }
        if ("ENROLLMENT".equals(mode)) {
            Long idUsuario = extractJsonLong(payload, "idUsuario");
            if (idUsuario != null) {
                state.enrollmentUserId = idUsuario;
                state.enrollmentSamples = 0;
                state.lastEnrollmentAt = null; // Reset para que la primera muestra sea inmediata
            }
        }
        // Al cambiar de modo, refrescar cache de embeddings
        state.embeddingsCache = plantillaService.getEmbeddingsByInstitucion(state.idInstitucion);
        state.cacheLoadedAt = Instant.now();

        sendText(session, "{\"status\":\"MODE_SET\",\"mode\":\"" + state.mode + "\"}");
    }

    // Mensaje binario (frame JPEG): detecta rostro y delega a reconocimiento o enrolamiento
    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        SessionState state = sessions.get(session.getId());
        if (state == null) return;

        try {
            // Refresh embeddings cache periodicamente para detectar nuevos enrolamientos
            if (Instant.now().isAfter(state.cacheLoadedAt.plusSeconds(CACHE_TTL_SECONDS))) {
                state.embeddingsCache = plantillaService.getEmbeddingsByInstitucion(state.idInstitucion);
                state.cacheLoadedAt = Instant.now();
            }

            byte[] frameBytes = new byte[message.getPayloadLength()];
            message.getPayload().get(frameBytes);

            // Detect face
            FaceRecognitionService.DetectionResult detection = faceService.detect(frameBytes);

            if (detection == null) {
                sendText(session, "{\"status\":\"NO_FACE\"}");
                return;
            }

            try {
                int[] box = detection.getFaceBox();
                LOG.debug("Rostro detectado: box=[{},{},{},{}]", box[0], box[1], box[2], box[3]);

                if ("ENROLLMENT".equals(state.mode)) {
                    handleEnrollment(session, state, detection, box);
                } else {
                    handleRecognition(session, state, detection, box);
                }
            } finally {
                detection.close();
            }
        } catch (Exception e) {
            LOG.error(">>> Error procesando frame: {}", e.getMessage(), e);
            sendText(session, "{\"status\":\"ERROR\",\"message\":\"Error interno: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    // Reconocimiento: identifica rostro, verifica cooldown y registra asistencia
    private void handleRecognition(WebSocketSession session, SessionState state,
                                    FaceRecognitionService.DetectionResult detection, int[] box) throws Exception {
        LOG.info(">>> handleRecognition: cache size={}, institucion={}", state.embeddingsCache.size(), state.idInstitucion);
        if (state.embeddingsCache.isEmpty()) {
            sendText(session, buildJson("UNKNOWN", box, null, null, "No hay profesores enrolados", null));
            return;
        }

        float[] embedding = faceService.extractEmbedding(detection.getAlignedFace());
        FaceRecognitionService.MatchResult match = faceService.identify(embedding, state.embeddingsCache);

        if (match == null) {
            sendText(session, buildJson("UNKNOWN", box, null, null, null, null));
            return;
        }

        Long idUsuario = match.getIdUsuario();

        // Check cooldown
        Instant lastReg = state.lastRegistration.get(idUsuario);
        if (lastReg != null && Instant.now().isBefore(lastReg.plusSeconds(COOLDOWN_SECONDS))) {
            sendText(session, buildJson("COOLDOWN", box, idUsuario, match.getScore(), null, null));
            return;
        }

        // Try to register attendance
        FacialAttendanceService.AttendanceResult result = attendanceService.registrarAsistenciaFacial(idUsuario);

        // Update cooldown
        state.lastRegistration.put(idUsuario, Instant.now());

        String responseStatus = switch (result.getStatus()) {
            case "REGISTRADA" -> "REGISTERED";
            case "YA_REGISTRADA" -> "ALREADY_REGISTERED";
            case "SIN_HORARIO" -> "NO_SCHEDULE";
            default -> "ERROR";
        };

        sendText(session, buildJson(responseStatus, box, idUsuario, match.getScore(),
                result.getMessage(), result.getIdAsistencia()));
    }

    // Enrolamiento: auto-captura con throttling (1 muestra cada 2s, max 15 muestras)
    private void handleEnrollment(WebSocketSession session, SessionState state,
                                   FaceRecognitionService.DetectionResult detection, int[] box) throws Exception {
        if (state.enrollmentUserId == null) {
            sendText(session, buildJson("ERROR", null, null, null,
                    "No se selecciono profesor para enrolamiento", null));
            return;
        }

        // Si ya se completaron las muestras, auto-switch a reconocimiento
        if (state.enrollmentSamples >= MAX_ENROLLMENT_SAMPLES) {
            state.mode = "RECOGNITION";
            // Refresh cache con las nuevas plantillas
            state.embeddingsCache = plantillaService.getEmbeddingsByInstitucion(state.idInstitucion);
            state.cacheLoadedAt = Instant.now();
            String json = "{\"status\":\"ENROLLMENT_COMPLETE\",\"faceBox\":" + arrayToJson(box)
                    + ",\"totalSamples\":" + state.enrollmentSamples
                    + ",\"idUsuario\":" + state.enrollmentUserId + "}";
            sendText(session, json);
            return;
        }

        // Throttle: solo capturar una muestra cada ENROLLMENT_INTERVAL_MS
        Instant now = Instant.now();
        if (state.lastEnrollmentAt != null &&
                now.isBefore(state.lastEnrollmentAt.plusMillis(ENROLLMENT_INTERVAL_MS))) {
            // Enviar face box sin enrolar (para que el usuario vea el recuadro)
            sendText(session, "{\"status\":\"ENROLLMENT_PREVIEW\",\"faceBox\":" + arrayToJson(box)
                    + ",\"sampleNumber\":" + state.enrollmentSamples
                    + ",\"maxSamples\":" + MAX_ENROLLMENT_SAMPLES + "}");
            return;
        }

        // Capturar muestra
        float[] embedding = faceService.extractEmbedding(detection.getAlignedFace());
        plantillaService.enrollFace(state.enrollmentUserId, embedding, faceService);
        state.enrollmentSamples++;
        state.lastEnrollmentAt = now;

        LOG.info(">>> Muestra {} de {} capturada para usuario {}", state.enrollmentSamples, MAX_ENROLLMENT_SAMPLES, state.enrollmentUserId);

        String json = "{\"status\":\"ENROLLED\",\"faceBox\":" + arrayToJson(box)
                + ",\"sampleNumber\":" + state.enrollmentSamples
                + ",\"maxSamples\":" + MAX_ENROLLMENT_SAMPLES
                + ",\"idUsuario\":" + state.enrollmentUserId + "}";
        sendText(session, json);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        LOG.info(">>> WS cerrado: sessionId={}, status={}", session.getId(), status);
        sessions.remove(session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        LOG.error(">>> WS error de transporte: {}", exception.getMessage(), exception);
        sessions.remove(session.getId());
    }

    private void sendText(WebSocketSession session, String json) throws Exception {
        if (session.isOpen()) {
            session.sendMessage(new TextMessage(json));
        }
    }

    private String buildJson(String status, int[] faceBox, Long idUsuario, Double score,
                              String message, Long idAsistencia) {
        StringBuilder sb = new StringBuilder("{\"status\":\"").append(status).append("\"");
        if (faceBox != null) {
            sb.append(",\"faceBox\":").append(arrayToJson(faceBox));
        }
        if (idUsuario != null) {
            sb.append(",\"idUsuario\":").append(idUsuario);
        }
        if (score != null) {
            sb.append(",\"score\":").append(score);
        }
        if (message != null) {
            sb.append(",\"message\":\"").append(escapeJson(message)).append("\"");
        }
        if (idAsistencia != null) {
            sb.append(",\"idAsistencia\":").append(idAsistencia);
        }
        sb.append("}");
        return sb.toString();
    }

    private String arrayToJson(int[] arr) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(arr[i]);
        }
        sb.append("]");
        return sb.toString();
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String extractToken(String query) {
        if (query == null) return null;
        for (String param : query.split("&")) {
            String[] kv = param.split("=", 2);
            if (kv.length == 2 && "token".equals(kv[0])) {
                return kv[1];
            }
        }
        return null;
    }

    private String extractJsonString(String json, String key) {
        String search = "\"" + key + "\":\"";
        int start = json.indexOf(search);
        if (start < 0) return null;
        start += search.length();
        int end = json.indexOf("\"", start);
        if (end < 0) return null;
        return json.substring(start, end);
    }

    private Long extractJsonLong(String json, String key) {
        String search = "\"" + key + "\":";
        int start = json.indexOf(search);
        if (start < 0) return null;
        start += search.length();
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (Character.isDigit(c) || c == '-') sb.append(c);
            else if (sb.length() > 0) break;
        }
        if (sb.length() == 0) return null;
        return Long.parseLong(sb.toString());
    }

    // Estado de cada sesion WebSocket activa
    private static class SessionState {
        Long idInstitucion;
        String nombreUsuario;
        String mode;
        Map<Long, float[]> embeddingsCache = new ConcurrentHashMap<>();
        Instant cacheLoadedAt = Instant.now();
        Map<Long, Instant> lastRegistration = new ConcurrentHashMap<>();
        Long enrollmentUserId;
        int enrollmentSamples;
        Instant lastEnrollmentAt;
    }
}
