package com.appasistencia.services;

import com.appasistencia.dtos.PlantillaBiometricaDTO;
import com.appasistencia.dtos.response.PlantillaBiometricaResponseDTO;
import com.appasistencia.exceptions.RecursoNoEncontradoException;
import com.appasistencia.models.PlantillaBiometrica;
import com.appasistencia.models.Usuario;
import com.appasistencia.repositories.PlantillaBiometricaRepository;
import com.appasistencia.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Servicio: gestion de plantillas biometricas (rostros enrolados)
@Service
@Transactional
public class PlantillaBiometricaService {

    private final PlantillaBiometricaRepository plantillaRepository;
    private final UsuarioRepository usuarioRepository;

    public PlantillaBiometricaService(PlantillaBiometricaRepository plantillaRepository,
                                       UsuarioRepository usuarioRepository) {
        this.plantillaRepository = plantillaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<PlantillaBiometricaResponseDTO> listarTodas() {
        return plantillaRepository.findByActivoTrue().stream()
                .map(PlantillaBiometricaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PlantillaBiometricaResponseDTO obtenerPorId(Long id) {
        return PlantillaBiometricaResponseDTO.fromEntity(buscarPorId(id));
    }

    @Transactional(readOnly = true)
    public PlantillaBiometrica buscarPorId(Long id) {
        return plantillaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("PlantillaBiometrica", id));
    }

    @Transactional(readOnly = true)
    public List<PlantillaBiometricaResponseDTO> listarPorUsuario(Long idUsuario) {
        return plantillaRepository.findByUsuarioIdUsuarioAndActivoTrue(idUsuario).stream()
                .map(PlantillaBiometricaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public PlantillaBiometricaResponseDTO crear(PlantillaBiometricaDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", dto.getIdUsuario()));

        PlantillaBiometrica plantilla = new PlantillaBiometrica(usuario, null, dto.getCantidadMuestras());
        return PlantillaBiometricaResponseDTO.fromEntity(plantillaRepository.save(plantilla));
    }

    // Eliminar (borrado logico)
    public void eliminar(Long id) {
        PlantillaBiometrica plantilla = buscarPorId(id);
        plantilla.setActivo(false);
        plantillaRepository.save(plantilla);
    }

    // Enrolar rostro: promedia con embedding existente o crea plantilla nueva
    public void enrollFace(Long idUsuario, float[] embedding, FaceRecognitionService faceService) {
        byte[] embeddingBytes = faceService.embeddingToBytes(embedding);

        List<PlantillaBiometrica> existing = plantillaRepository.findByUsuarioIdUsuarioAndActivoTrue(idUsuario);

        if (!existing.isEmpty()) {
            PlantillaBiometrica plantilla = existing.get(0);
            if (plantilla.getModeloFacial() != null && plantilla.getModeloFacial().length > 0) {
                // Average with existing embedding
                float[] existingEmb = faceService.bytesToEmbedding(plantilla.getModeloFacial());
                float[] averaged = faceService.averageEmbeddings(existingEmb, embedding, plantilla.getCantidadMuestras());
                plantilla.setModeloFacial(faceService.embeddingToBytes(averaged));
            } else {
                plantilla.setModeloFacial(embeddingBytes);
            }
            plantilla.setCantidadMuestras(plantilla.getCantidadMuestras() + 1);
            plantillaRepository.save(plantilla);
        } else {
            Usuario usuario = usuarioRepository.findById(idUsuario)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", idUsuario));
            PlantillaBiometrica plantilla = new PlantillaBiometrica(usuario, embeddingBytes, 1);
            plantillaRepository.save(plantilla);
        }
    }

    // Obtener embeddings de una institucion mapeados por idUsuario
    @Transactional(readOnly = true)
    public Map<Long, float[]> getEmbeddingsByInstitucion(Long idInstitucion) {
        List<PlantillaBiometrica> plantillas = plantillaRepository.findByInstitucion(idInstitucion);
        Map<Long, float[]> result = new HashMap<>();

        FaceRecognitionService faceService = null;
        // We need to convert bytes to floats - use a simple ByteBuffer approach
        for (PlantillaBiometrica p : plantillas) {
            if (p.getModeloFacial() != null && p.getModeloFacial().length > 0 && p.getUsuario() != null) {
                java.nio.FloatBuffer fb = java.nio.ByteBuffer.wrap(p.getModeloFacial()).asFloatBuffer();
                float[] emb = new float[fb.remaining()];
                fb.get(emb);
                result.put(p.getUsuario().getIdUsuario(), emb);
            }
        }
        return result;
    }
}
