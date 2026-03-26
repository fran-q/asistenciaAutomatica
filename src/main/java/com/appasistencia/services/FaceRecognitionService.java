package com.appasistencia.services;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_objdetect.FaceDetectorYN;
import org.bytedeco.opencv.opencv_objdetect.FaceRecognizerSF;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Servicio: reconocimiento facial via modelos ONNX (YuNet + SFace)
@Service
public class FaceRecognitionService {

    private static final Logger LOG = LoggerFactory.getLogger(FaceRecognitionService.class);

    private static final int INPUT_WIDTH = 320;
    private static final int INPUT_HEIGHT = 320;
    private static final float SCORE_THRESHOLD = 0.7f;
    private static final float NMS_THRESHOLD = 0.3f;
    private static final int TOP_K = 5000;
    private static final double COSINE_THRESHOLD = 0.363;
    private static final int EMBEDDING_DIM = 128;

    private FaceDetectorYN detector;
    private FaceRecognizerSF recognizer;
    private Path tempRecognizerPath;

    // Cargar modelos ONNX de deteccion y reconocimiento al iniciar
    @PostConstruct
    public void init() throws IOException {
        // FaceDetectorYN: use buffer-based create (no file-path overload in JavaCV 4.10)
        byte[] detectorModelBytes;
        try (InputStream is = new ClassPathResource("models/face_detection_yunet_2023mar.onnx").getInputStream()) {
            detectorModelBytes = is.readAllBytes();
        }
        detector = FaceDetectorYN.create(
                "onnx", detectorModelBytes, new byte[0],
                new Size(INPUT_WIDTH, INPUT_HEIGHT),
                SCORE_THRESHOLD, NMS_THRESHOLD, TOP_K, 0, 0);

        // FaceRecognizerSF: uses (String, String) file-path create
        tempRecognizerPath = extractModel("models/face_recognition_sface_2021dec.onnx");
        recognizer = FaceRecognizerSF.create(
                tempRecognizerPath.toString(), "");
    }

    @PreDestroy
    public void cleanup() {
        if (detector != null) detector.close();
        if (recognizer != null) recognizer.close();
        try {
            if (tempRecognizerPath != null) Files.deleteIfExists(tempRecognizerPath);
        } catch (IOException ignored) {}
    }

    private Path extractModel(String classpathLocation) throws IOException {
        ClassPathResource resource = new ClassPathResource(classpathLocation);
        Path tempFile = Files.createTempFile("opencv_model_", ".onnx");
        try (InputStream is = resource.getInputStream()) {
            Files.copy(is, tempFile, StandardCopyOption.REPLACE_EXISTING);
        }
        return tempFile;
    }

    // Detectar rostro en frame JPEG; retorna bounding box + rostro alineado
    public synchronized DetectionResult detect(byte[] jpegFrame) {
        // Decode JPEG bytes to Mat using BytePointer
        BytePointer bp = new BytePointer(jpegFrame);
        Mat raw = new Mat(1, jpegFrame.length, org.bytedeco.opencv.global.opencv_core.CV_8UC1, bp);
        Mat frame = opencv_imgcodecs.imdecode(raw, opencv_imgcodecs.IMREAD_COLOR);
        bp.close();
        raw.close();

        if (frame.empty()) {
            frame.close();
            return null;
        }

        // Resize to detector input size
        Mat resized = new Mat();
        opencv_imgproc.resize(frame, resized, new Size(INPUT_WIDTH, INPUT_HEIGHT));

        // Set input size and detect
        detector.setInputSize(new Size(INPUT_WIDTH, INPUT_HEIGHT));
        Mat faces = new Mat();
        detector.detect(resized, faces);

        if (faces.empty() || faces.rows() == 0) {
            faces.close();
            resized.close();
            frame.close();
            return null;
        }

        // Take the first detected face (highest confidence)
        // faces is Nx15: [x,y,w,h, x_re,y_re, x_le,y_le, x_nt,y_nt, x_rcm,y_rcm, x_lcm,y_lcm, score]
        float[] faceData = new float[15];
        FloatPointer fp = new FloatPointer(faces.row(0).ptr());
        fp.get(faceData);

        // Scale bounding box back to original frame dimensions
        float scaleX = (float) frame.cols() / INPUT_WIDTH;
        float scaleY = (float) frame.rows() / INPUT_HEIGHT;
        int[] faceBox = new int[]{
                Math.round(faceData[0] * scaleX),
                Math.round(faceData[1] * scaleY),
                Math.round(faceData[2] * scaleX),
                Math.round(faceData[3] * scaleY)
        };

        // Align face for recognition using the RESIZED frame
        // (las coordenadas de faces.row(0) estan en espacio 320x320, deben coincidir con la imagen)
        Mat alignedFace = new Mat();
        recognizer.alignCrop(resized, faces.row(0), alignedFace);

        faces.close();
        resized.close();

        return new DetectionResult(faceBox, alignedFace, frame);
    }

    // Extraer embedding de 128 dimensiones del rostro alineado
    public float[] extractEmbedding(Mat alignedFace) {
        Mat feature = new Mat();
        recognizer.feature(alignedFace, feature);

        float[] embedding = new float[EMBEDDING_DIM];
        FloatPointer fp = new FloatPointer(feature.ptr());
        fp.get(embedding);
        feature.close();

        return embedding;
    }

    // Identificar rostro comparando por similitud coseno contra embeddings conocidos
    public MatchResult identify(float[] embedding, Map<Long, float[]> knownEmbeddings) {
        Long bestId = null;
        double bestScore = -1;

        for (Map.Entry<Long, float[]> entry : knownEmbeddings.entrySet()) {
            double score = cosineSimilarity(embedding, entry.getValue());
            LOG.debug(">>> Cosine similarity con usuario {}: {}", entry.getKey(), String.format("%.4f", score));
            if (score > bestScore) {
                bestScore = score;
                bestId = entry.getKey();
            }
        }

        LOG.info(">>> Mejor match: usuario={}, score={}, threshold={}", bestId, String.format("%.4f", bestScore), COSINE_THRESHOLD);

        if (bestId != null && bestScore >= COSINE_THRESHOLD) {
            return new MatchResult(bestId, bestScore);
        }
        return null;
    }

    private double cosineSimilarity(float[] a, float[] b) {
        double dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    // Convertir embedding float[] a byte[] para almacenar en BD
    public byte[] embeddingToBytes(float[] embedding) {
        ByteBuffer buffer = ByteBuffer.allocate(embedding.length * 4);
        buffer.asFloatBuffer().put(embedding);
        return buffer.array();
    }

    // Convertir byte[] de BD a embedding float[]
    public float[] bytesToEmbedding(byte[] bytes) {
        FloatBuffer fb = ByteBuffer.wrap(bytes).asFloatBuffer();
        float[] embedding = new float[fb.remaining()];
        fb.get(embedding);
        return embedding;
    }

    // Promediar embeddings para enrolamiento con multiples muestras
    public float[] averageEmbeddings(float[] existing, float[] newEmb, int existingCount) {
        float[] result = new float[EMBEDDING_DIM];
        for (int i = 0; i < EMBEDDING_DIM; i++) {
            result[i] = (existing[i] * existingCount + newEmb[i]) / (existingCount + 1);
        }
        // Normalize
        float norm = 0;
        for (float v : result) norm += v * v;
        norm = (float) Math.sqrt(norm);
        if (norm > 0) {
            for (int i = 0; i < result.length; i++) result[i] /= norm;
        }
        return result;
    }

    // Resultado de deteccion: bounding box + rostro alineado
    public static class DetectionResult {
        private final int[] faceBox; // x, y, w, h
        private final Mat alignedFace;
        private final Mat originalFrame;

        public DetectionResult(int[] faceBox, Mat alignedFace, Mat originalFrame) {
            this.faceBox = faceBox;
            this.alignedFace = alignedFace;
            this.originalFrame = originalFrame;
        }

        public int[] getFaceBox() { return faceBox; }
        public Mat getAlignedFace() { return alignedFace; }
        public Mat getOriginalFrame() { return originalFrame; }

        public void close() {
            if (alignedFace != null) alignedFace.close();
            if (originalFrame != null) originalFrame.close();
        }
    }

    // Resultado de identificacion: idUsuario + puntaje de similitud
    public static class MatchResult {
        private final Long idUsuario;
        private final double score;

        public MatchResult(Long idUsuario, double score) {
            this.idUsuario = idUsuario;
            this.score = score;
        }

        public Long getIdUsuario() { return idUsuario; }
        public double getScore() { return score; }
    }
}
