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

@Service
@Transactional
public class UsuarioAlumnoService {

    private final UsuarioAlumnoRepository alumnoRepository;
    private final UsuarioRepository usuarioRepository;

    public UsuarioAlumnoService(UsuarioAlumnoRepository alumnoRepository, UsuarioRepository usuarioRepository) {
        this.alumnoRepository = alumnoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<UsuarioAlumnoResponseDTO> listarTodos() {
        return alumnoRepository.findByActivoTrue().stream()
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

    public UsuarioAlumnoResponseDTO actualizar(Long id, UsuarioAlumnoDTO dto) {
        UsuarioAlumno alumno = buscarPorId(id);
        alumno.setLegajo(dto.getLegajo());
        alumno.setPromedio(dto.getPromedio());
        return UsuarioAlumnoResponseDTO.fromEntity(alumnoRepository.save(alumno));
    }

    public void eliminar(Long id) {
        UsuarioAlumno alumno = buscarPorId(id);
        alumno.setActivo(false);
        alumnoRepository.save(alumno);
    }
}
