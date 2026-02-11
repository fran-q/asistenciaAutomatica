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

import java.util.List;
import java.util.stream.Collectors;

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

    public void eliminar(Long id) {
        PlantillaBiometrica plantilla = buscarPorId(id);
        plantilla.setActivo(false);
        plantillaRepository.save(plantilla);
    }
}
