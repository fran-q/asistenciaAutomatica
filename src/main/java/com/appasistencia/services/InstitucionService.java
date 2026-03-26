package com.appasistencia.services;

import com.appasistencia.dtos.InstitucionDTO;
import com.appasistencia.dtos.response.InstitucionResponseDTO;
import com.appasistencia.exceptions.RecursoDuplicadoException;
import com.appasistencia.exceptions.RecursoNoEncontradoException;
import com.appasistencia.models.Institucion;
import com.appasistencia.repositories.InstitucionRepository;
import com.appasistencia.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Servicio: logica de negocio para instituciones educativas
@Service
@Transactional
public class InstitucionService {

    private final InstitucionRepository institucionRepository;
    private final UsuarioRepository usuarioRepository;

    public InstitucionService(InstitucionRepository institucionRepository,
                              UsuarioRepository usuarioRepository) {
        this.institucionRepository = institucionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // CRUD
    @Transactional(readOnly = true)
    public List<InstitucionResponseDTO> listarTodas() {
        return institucionRepository.findAll().stream()
                .map(InstitucionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InstitucionResponseDTO obtenerPorId(Long id) {
        Institucion institucion = buscarPorId(id);
        return InstitucionResponseDTO.fromEntity(institucion);
    }

    public InstitucionResponseDTO crear(InstitucionDTO dto) {
        // Validar unicidad cruzada del email (contra usuarios y otras instituciones)
        validarEmailUnico(dto.getEmail(), null);
        Institucion institucion = new Institucion(
                dto.getNombre(), dto.getDireccion(), dto.getTelefono(), dto.getEmail()
        );
        return InstitucionResponseDTO.fromEntity(institucionRepository.save(institucion));
    }

    public InstitucionResponseDTO actualizar(Long id, InstitucionDTO dto) {
        Institucion institucion = buscarPorId(id);
        // Validar unicidad cruzada del email si cambio
        validarEmailUnico(dto.getEmail(), id);
        institucion.setNombre(dto.getNombre());
        institucion.setDireccion(dto.getDireccion());
        institucion.setTelefono(dto.getTelefono());
        institucion.setEmail(dto.getEmail());
        return InstitucionResponseDTO.fromEntity(institucionRepository.save(institucion));
    }

    // Valida que el email no este usado por ningun usuario ni otra institucion
    private void validarEmailUnico(String email, Long excludeInstitucionId) {
        if (email == null || email.isBlank()) return;
        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new RecursoDuplicadoException("Este email ya esta registrado como usuario");
        }
        Optional<Institucion> existente = institucionRepository.findByEmail(email);
        if (existente.isPresent() && (excludeInstitucionId == null || !existente.get().getIdInstitucion().equals(excludeInstitucionId))) {
            throw new RecursoDuplicadoException("Ya existe una institucion con el email: " + email);
        }
    }

    // Baja logica y reactivacion
    public void eliminar(Long id) {
        Institucion institucion = buscarPorId(id);
        institucion.setActivo(false);
        institucionRepository.save(institucion);
    }

    public void reactivar(Long id) {
        Institucion institucion = buscarPorId(id);
        institucion.setActivo(true);
        institucionRepository.save(institucion);
    }

    // Busqueda interna (retorna entidad, no DTO)
    public Institucion buscarPorId(Long id) {
        return institucionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Institución", id));
    }
}
