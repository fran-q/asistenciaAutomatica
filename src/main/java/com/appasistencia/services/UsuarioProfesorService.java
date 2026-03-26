package com.appasistencia.services;

import com.appasistencia.dtos.UsuarioProfesorDTO;
import com.appasistencia.dtos.response.UsuarioProfesorResponseDTO;
import com.appasistencia.exceptions.RecursoDuplicadoException;
import com.appasistencia.exceptions.RecursoNoEncontradoException;
import com.appasistencia.exceptions.OperacionInvalidaException;
import com.appasistencia.models.CategoriaProfesor;
import com.appasistencia.models.Rol;
import com.appasistencia.models.Usuario;
import com.appasistencia.models.UsuarioProfesor;
import com.appasistencia.repositories.UsuarioProfesorRepository;
import com.appasistencia.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// Servicio: logica de negocio para perfiles de profesor (vinculados a Usuario)
@Service
@Transactional
public class UsuarioProfesorService {

    private final UsuarioProfesorRepository profesorRepository;
    private final UsuarioRepository usuarioRepository;

    public UsuarioProfesorService(UsuarioProfesorRepository profesorRepository, UsuarioRepository usuarioRepository) {
        this.profesorRepository = profesorRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Listado (todos o filtrados por institucion)
    @Transactional(readOnly = true)
    public List<UsuarioProfesorResponseDTO> listarTodos() {
        return profesorRepository.findAll().stream()
                .map(UsuarioProfesorResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UsuarioProfesorResponseDTO> listarTodos(Long idInstitucion) {
        return profesorRepository.findByInstitucion(idInstitucion).stream()
                .map(UsuarioProfesorResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioProfesorResponseDTO obtenerPorId(Long id) {
        return UsuarioProfesorResponseDTO.fromEntity(buscarPorId(id));
    }

    @Transactional(readOnly = true)
    public UsuarioProfesor buscarPorId(Long id) {
        return profesorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Profesor", id));
    }

    // Crear perfil: valida rol PROFESOR, unicidad de legajo y perfil unico por usuario
    public UsuarioProfesorResponseDTO crear(UsuarioProfesorDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", dto.getIdUsuario()));

        if (usuario.getRol() != Rol.PROFESOR) {
            throw new OperacionInvalidaException("El usuario con ID " + dto.getIdUsuario() + " no tiene rol PROFESOR");
        }

        if (profesorRepository.findByUsuarioIdUsuario(dto.getIdUsuario()).isPresent()) {
            throw new RecursoDuplicadoException("El usuario ya tiene un perfil de profesor asociado");
        }

        if (profesorRepository.findByLegajo(dto.getLegajo()).isPresent()) {
            throw new RecursoDuplicadoException("Ya existe un profesor con el legajo: " + dto.getLegajo());
        }

        try {
            UsuarioProfesor profesor = new UsuarioProfesor(
                    usuario, dto.getLegajo(), dto.getTitulo(),
                    CategoriaProfesor.valueOf(dto.getCategoria())
            );
            return UsuarioProfesorResponseDTO.fromEntity(profesorRepository.save(profesor));
        } catch (IllegalArgumentException e) {
            throw new OperacionInvalidaException("Categoria invalida: " + dto.getCategoria() + ". Valores permitidos: TITULAR, ADJUNTO, SUPLENTE, INTERINO");
        }
    }

    // Actualizar perfil: valida unicidad de legajo si cambio
    public UsuarioProfesorResponseDTO actualizar(Long id, UsuarioProfesorDTO dto) {
        UsuarioProfesor profesor = buscarPorId(id);

        // Si el legajo cambio, verificar que no este en uso por otro profesor
        if (!profesor.getLegajo().equals(dto.getLegajo())) {
            profesorRepository.findByLegajo(dto.getLegajo()).ifPresent(otro -> {
                if (!otro.getIdProfesor().equals(id)) {
                    throw new RecursoDuplicadoException("Ya existe un profesor con el legajo: " + dto.getLegajo());
                }
            });
        }

        profesor.setLegajo(dto.getLegajo());
        profesor.setTitulo(dto.getTitulo());
        if (dto.getCategoria() != null) {
            try {
                profesor.setCategoria(CategoriaProfesor.valueOf(dto.getCategoria()));
            } catch (IllegalArgumentException e) {
                throw new OperacionInvalidaException("Categoria invalida: " + dto.getCategoria());
            }
        }
        return UsuarioProfesorResponseDTO.fromEntity(profesorRepository.save(profesor));
    }

    // Baja logica y reactivacion
    public void eliminar(Long id) {
        UsuarioProfesor profesor = buscarPorId(id);
        profesor.setActivo(false);
        profesorRepository.save(profesor);
    }

    public void reactivar(Long id) {
        UsuarioProfesor profesor = buscarPorId(id);
        profesor.setActivo(true);
        profesorRepository.save(profesor);
    }

    // === Metodos con validacion de institucion ===
    // Estos metodos verifican que el recurso pertenezca a la institucion del usuario autenticado

    // Verifica que el recurso pertenezca a la institucion del usuario autenticado
    private void verificarInstitucion(Long idInstitucionRecurso, Long idInstitucionUsuario) {
        if (!idInstitucionRecurso.equals(idInstitucionUsuario)) {
            throw new RecursoNoEncontradoException("Profesor", 0L);
        }
    }

    // Obtener profesor por ID validando que pertenece a la misma institucion
    @Transactional(readOnly = true)
    public UsuarioProfesorResponseDTO obtenerPorId(Long id, Long idInstitucion) {
        UsuarioProfesor profesor = buscarPorId(id);
        verificarInstitucion(profesor.getUsuario().getInstitucion().getIdInstitucion(), idInstitucion);
        return UsuarioProfesorResponseDTO.fromEntity(profesor);
    }

    // Actualizar profesor validando que pertenece a la misma institucion
    public UsuarioProfesorResponseDTO actualizar(Long id, UsuarioProfesorDTO dto, Long idInstitucion) {
        UsuarioProfesor profesor = buscarPorId(id);
        verificarInstitucion(profesor.getUsuario().getInstitucion().getIdInstitucion(), idInstitucion);
        return actualizar(id, dto);
    }

    // Eliminar profesor validando que pertenece a la misma institucion
    public void eliminar(Long id, Long idInstitucion) {
        UsuarioProfesor profesor = buscarPorId(id);
        verificarInstitucion(profesor.getUsuario().getInstitucion().getIdInstitucion(), idInstitucion);
        eliminar(id);
    }

    // Reactivar profesor validando que pertenece a la misma institucion
    public void reactivar(Long id, Long idInstitucion) {
        UsuarioProfesor profesor = buscarPorId(id);
        verificarInstitucion(profesor.getUsuario().getInstitucion().getIdInstitucion(), idInstitucion);
        reactivar(id);
    }
}
