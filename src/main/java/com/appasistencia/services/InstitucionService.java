package com.appasistencia.services;

import com.appasistencia.dtos.InstitucionDTO;
import com.appasistencia.dtos.response.InstitucionResponseDTO;
import com.appasistencia.exceptions.RecursoNoEncontradoException;
import com.appasistencia.models.Institucion;
import com.appasistencia.repositories.InstitucionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InstitucionService {

    private final InstitucionRepository institucionRepository;

    public InstitucionService(InstitucionRepository institucionRepository) {
        this.institucionRepository = institucionRepository;
    }

    @Transactional(readOnly = true)
    public List<InstitucionResponseDTO> listarTodas() {
        return institucionRepository.findByActivoTrue().stream()
                .map(InstitucionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InstitucionResponseDTO obtenerPorId(Long id) {
        Institucion institucion = buscarPorId(id);
        return InstitucionResponseDTO.fromEntity(institucion);
    }

    public InstitucionResponseDTO crear(InstitucionDTO dto) {
        Institucion institucion = new Institucion(
                dto.getNombre(), dto.getDireccion(), dto.getTelefono(), dto.getEmail()
        );
        return InstitucionResponseDTO.fromEntity(institucionRepository.save(institucion));
    }

    public InstitucionResponseDTO actualizar(Long id, InstitucionDTO dto) {
        Institucion institucion = buscarPorId(id);
        institucion.setNombre(dto.getNombre());
        institucion.setDireccion(dto.getDireccion());
        institucion.setTelefono(dto.getTelefono());
        institucion.setEmail(dto.getEmail());
        return InstitucionResponseDTO.fromEntity(institucionRepository.save(institucion));
    }

    public void eliminar(Long id) {
        Institucion institucion = buscarPorId(id);
        institucion.setActivo(false);
        institucionRepository.save(institucion);
    }

    public Institucion buscarPorId(Long id) {
        return institucionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Institución", id));
    }
}
