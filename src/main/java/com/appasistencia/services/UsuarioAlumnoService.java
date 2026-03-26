package com.appasistencia.services;

import com.appasistencia.dtos.UsuarioAlumnoDTO;
import com.appasistencia.dtos.response.UsuarioAlumnoResponseDTO;
import com.appasistencia.exceptions.RecursoDuplicadoException;
import com.appasistencia.exceptions.RecursoNoEncontradoException;
import com.appasistencia.exceptions.OperacionInvalidaException;
import com.appasistencia.models.Rol;
import com.appasistencia.models.Usuario;
import com.appasistencia.models.UsuarioAlumno;
import com.appasistencia.repositories.UsuarioAlumnoRepository;
import com.appasistencia.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// Servicio: logica de negocio para perfiles de alumno (vinculados a Usuario)
@Service
@Transactional
public class UsuarioAlumnoService {

    private final UsuarioAlumnoRepository alumnoRepository;
    private final UsuarioRepository usuarioRepository;

    public UsuarioAlumnoService(UsuarioAlumnoRepository alumnoRepository, UsuarioRepository usuarioRepository) {
        this.alumnoRepository = alumnoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Listado (todos o filtrados por institucion)
    @Transactional(readOnly = true)
    public List<UsuarioAlumnoResponseDTO> listarTodos() {
        return alumnoRepository.findAll().stream()
                .map(UsuarioAlumnoResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UsuarioAlumnoResponseDTO> listarTodos(Long idInstitucion) {
        return alumnoRepository.findByInstitucion(idInstitucion).stream()
                .map(UsuarioAlumnoResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioAlumnoResponseDTO obtenerPorId(Long id) {
        return UsuarioAlumnoResponseDTO.fromEntity(buscarPorId(id));
    }

    @Transactional(readOnly = true)
    public UsuarioAlumno buscarPorId(Long id) {
        return alumnoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Alumno", id));
    }

    // Crear perfil: valida rol ALUMNO, unicidad de legajo y perfil unico por usuario
    public UsuarioAlumnoResponseDTO crear(UsuarioAlumnoDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", dto.getIdUsuario()));

        if (usuario.getRol() != Rol.ALUMNO) {
            throw new OperacionInvalidaException("El usuario con ID " + dto.getIdUsuario() + " no tiene rol ALUMNO");
        }

        if (alumnoRepository.findByUsuarioIdUsuario(dto.getIdUsuario()).isPresent()) {
            throw new RecursoDuplicadoException("El usuario ya tiene un perfil de alumno asociado");
        }

        if (alumnoRepository.findByLegajo(dto.getLegajo()).isPresent()) {
            throw new RecursoDuplicadoException("Ya existe un alumno con el legajo: " + dto.getLegajo());
        }

        UsuarioAlumno alumno = new UsuarioAlumno(usuario, dto.getLegajo(), dto.getPromedio());
        return UsuarioAlumnoResponseDTO.fromEntity(alumnoRepository.save(alumno));
    }

    // Actualizar perfil: valida unicidad de legajo si cambio
    public UsuarioAlumnoResponseDTO actualizar(Long id, UsuarioAlumnoDTO dto) {
        UsuarioAlumno alumno = buscarPorId(id);

        // Si el legajo cambio, verificar que no este en uso por otro alumno
        if (!alumno.getLegajo().equals(dto.getLegajo())) {
            alumnoRepository.findByLegajo(dto.getLegajo()).ifPresent(otro -> {
                if (!otro.getIdAlumno().equals(id)) {
                    throw new RecursoDuplicadoException("Ya existe un alumno con el legajo: " + dto.getLegajo());
                }
            });
        }

        alumno.setLegajo(dto.getLegajo());
        alumno.setPromedio(dto.getPromedio());
        return UsuarioAlumnoResponseDTO.fromEntity(alumnoRepository.save(alumno));
    }

    // Baja logica y reactivacion
    public void eliminar(Long id) {
        UsuarioAlumno alumno = buscarPorId(id);
        alumno.setActivo(false);
        alumnoRepository.save(alumno);
    }

    public void reactivar(Long id) {
        UsuarioAlumno alumno = buscarPorId(id);
        alumno.setActivo(true);
        alumnoRepository.save(alumno);
    }

    // === Metodos con validacion de institucion ===
    // Estos metodos verifican que el recurso pertenezca a la institucion del usuario autenticado

    // Verifica que el recurso pertenezca a la institucion del usuario autenticado
    private void verificarInstitucion(Long idInstitucionRecurso, Long idInstitucionUsuario) {
        if (!idInstitucionRecurso.equals(idInstitucionUsuario)) {
            throw new RecursoNoEncontradoException("Alumno", 0L);
        }
    }

    // Obtener alumno por ID validando que pertenece a la misma institucion
    @Transactional(readOnly = true)
    public UsuarioAlumnoResponseDTO obtenerPorId(Long id, Long idInstitucion) {
        UsuarioAlumno alumno = buscarPorId(id);
        verificarInstitucion(alumno.getUsuario().getInstitucion().getIdInstitucion(), idInstitucion);
        return UsuarioAlumnoResponseDTO.fromEntity(alumno);
    }

    // Actualizar alumno validando que pertenece a la misma institucion
    public UsuarioAlumnoResponseDTO actualizar(Long id, UsuarioAlumnoDTO dto, Long idInstitucion) {
        UsuarioAlumno alumno = buscarPorId(id);
        verificarInstitucion(alumno.getUsuario().getInstitucion().getIdInstitucion(), idInstitucion);
        return actualizar(id, dto);
    }

    // Eliminar alumno validando que pertenece a la misma institucion
    public void eliminar(Long id, Long idInstitucion) {
        UsuarioAlumno alumno = buscarPorId(id);
        verificarInstitucion(alumno.getUsuario().getInstitucion().getIdInstitucion(), idInstitucion);
        eliminar(id);
    }

    // Reactivar alumno validando que pertenece a la misma institucion
    public void reactivar(Long id, Long idInstitucion) {
        UsuarioAlumno alumno = buscarPorId(id);
        verificarInstitucion(alumno.getUsuario().getInstitucion().getIdInstitucion(), idInstitucion);
        reactivar(id);
    }
}
