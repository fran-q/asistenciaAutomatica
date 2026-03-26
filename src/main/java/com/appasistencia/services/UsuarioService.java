package com.appasistencia.services;

import com.appasistencia.dtos.UsuarioDTO;
import com.appasistencia.dtos.response.UsuarioResponseDTO;
import com.appasistencia.exceptions.RecursoDuplicadoException;
import com.appasistencia.exceptions.RecursoNoEncontradoException;
import com.appasistencia.exceptions.OperacionInvalidaException;
import com.appasistencia.models.*;
import com.appasistencia.repositories.InstitucionRepository;
import com.appasistencia.repositories.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Servicio: logica de negocio para usuarios (base de profesores, alumnos y admins)
@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final InstitucionRepository institucionRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          InstitucionRepository institucionRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.institucionRepository = institucionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Listado (todos o filtrados por institucion)
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos(Long idInstitucion) {
        return usuarioRepository.findByInstitucionIdInstitucion(idInstitucion).stream()
                .map(UsuarioResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerPorId(Long id) {
        Usuario usuario = buscarPorId(id);
        return UsuarioResponseDTO.fromEntity(usuario);
    }

    // Filtrado por rol (con o sin institucion)
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarPorRol(String rol) {
        try {
            Rol rolEnum = Rol.valueOf(rol.toUpperCase());
            return usuarioRepository.findByRolAndActivoTrue(rolEnum).stream()
                    .map(UsuarioResponseDTO::fromEntity)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new OperacionInvalidaException("Rol inválido: " + rol + ". Valores permitidos: ADMIN, PROFESOR, ALUMNO");
        }
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarPorRol(String rol, Long idInstitucion) {
        try {
            Rol rolEnum = Rol.valueOf(rol.toUpperCase());
            return usuarioRepository.findByRolAndInstitucionIdInstitucion(rolEnum, idInstitucion).stream()
                    .map(UsuarioResponseDTO::fromEntity)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new OperacionInvalidaException("Rol inválido: " + rol + ". Valores permitidos: ADMIN, PROFESOR, ALUMNO");
        }
    }

    // Crear usuario validando unicidad de email (cruzada con instituciones) y documento
    public UsuarioResponseDTO crear(UsuarioDTO dto) {
        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RecursoDuplicadoException("Ya existe un usuario con el email: " + dto.getEmail());
        }
        if (institucionRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RecursoDuplicadoException("Este email ya esta registrado como contacto de una institucion");
        }
        if (usuarioRepository.findByNumeroDocumento(dto.getNumeroDocumento()).isPresent()) {
            throw new RecursoDuplicadoException("El documento ingresado no se puede registrar, por favor ingrese otro");
        }

        Institucion institucion = institucionRepository.findById(dto.getIdInstitucion())
                .orElseThrow(() -> new RecursoNoEncontradoException("Institución", dto.getIdInstitucion()));

        Usuario usuario = new Usuario(
                dto.getNombre(), dto.getApellido(), dto.getEmail(), dto.getTelefono(),
                dto.getDireccion(), TipoDocumento.valueOf(dto.getTipoDocumento()),
                dto.getNumeroDocumento(), Genero.valueOf(dto.getGenero()),
                passwordEncoder.encode(dto.getContrasena()), Rol.valueOf(dto.getRol())
        );
        usuario.setFotoPerfil(dto.getFotoPerfil());
        usuario.setInstitucion(institucion);

        return UsuarioResponseDTO.fromEntity(usuarioRepository.save(usuario));
    }

    // Actualizar usuario (re-valida unicidad si email/documento cambian)
    public UsuarioResponseDTO actualizar(Long id, UsuarioDTO dto) {
        Usuario usuario = buscarPorId(id);

        // Validar unicidad de email cruzada (usuarios + instituciones) si cambio
        if (!usuario.getEmail().equals(dto.getEmail())) {
            Optional<Usuario> existente = usuarioRepository.findByEmail(dto.getEmail());
            if (existente.isPresent() && !existente.get().getIdUsuario().equals(id)) {
                throw new RecursoDuplicadoException("Ya existe un usuario con el email: " + dto.getEmail());
            }
            if (institucionRepository.findByEmail(dto.getEmail()).isPresent()) {
                throw new RecursoDuplicadoException("Este email ya esta registrado como contacto de una institucion");
            }
        }

        // Validar unicidad de documento si cambió
        if (!usuario.getNumeroDocumento().equals(dto.getNumeroDocumento())) {
            Optional<Usuario> existente = usuarioRepository.findByNumeroDocumento(dto.getNumeroDocumento());
            if (existente.isPresent() && !existente.get().getIdUsuario().equals(id)) {
                throw new RecursoDuplicadoException("El documento ingresado no se puede registrar, por favor ingrese otro");
            }
        }

        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setEmail(dto.getEmail());
        usuario.setTelefono(dto.getTelefono());
        usuario.setDireccion(dto.getDireccion());
        if (dto.getTipoDocumento() != null) usuario.setTipoDocumento(TipoDocumento.valueOf(dto.getTipoDocumento()));
        usuario.setNumeroDocumento(dto.getNumeroDocumento());
        if (dto.getGenero() != null) usuario.setGenero(Genero.valueOf(dto.getGenero()));
        if (dto.getContrasena() != null && !dto.getContrasena().isBlank()) {
            usuario.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        }
        if (dto.getRol() != null) usuario.setRol(Rol.valueOf(dto.getRol()));
        usuario.setFotoPerfil(dto.getFotoPerfil());

        if (dto.getIdInstitucion() != null) {
            Institucion institucion = institucionRepository.findById(dto.getIdInstitucion())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Institución", dto.getIdInstitucion()));
            usuario.setInstitucion(institucion);
        }

        return UsuarioResponseDTO.fromEntity(usuarioRepository.save(usuario));
    }

    // Baja logica y reactivacion
    public void eliminar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    public void reactivar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", id));
    }

    // === Metodos con validacion de institucion ===
    // Estos metodos verifican que el recurso pertenezca a la institucion del usuario autenticado

    // Verifica que el recurso pertenezca a la institucion del usuario autenticado
    private void verificarInstitucion(Long idInstitucionRecurso, Long idInstitucionUsuario) {
        if (!idInstitucionRecurso.equals(idInstitucionUsuario)) {
            throw new RecursoNoEncontradoException("Usuario", 0L);
        }
    }

    // Obtener usuario por ID validando que pertenece a la misma institucion
    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerPorId(Long id, Long idInstitucion) {
        Usuario usuario = buscarPorId(id);
        verificarInstitucion(usuario.getInstitucion().getIdInstitucion(), idInstitucion);
        return UsuarioResponseDTO.fromEntity(usuario);
    }

    // Actualizar usuario validando que pertenece a la misma institucion
    public UsuarioResponseDTO actualizar(Long id, UsuarioDTO dto, Long idInstitucion) {
        Usuario usuario = buscarPorId(id);
        verificarInstitucion(usuario.getInstitucion().getIdInstitucion(), idInstitucion);
        return actualizar(id, dto);
    }

    // Eliminar usuario validando que pertenece a la misma institucion
    public void eliminar(Long id, Long idInstitucion) {
        Usuario usuario = buscarPorId(id);
        verificarInstitucion(usuario.getInstitucion().getIdInstitucion(), idInstitucion);
        eliminar(id);
    }

    // Reactivar usuario validando que pertenece a la misma institucion
    public void reactivar(Long id, Long idInstitucion) {
        Usuario usuario = buscarPorId(id);
        verificarInstitucion(usuario.getInstitucion().getIdInstitucion(), idInstitucion);
        reactivar(id);
    }
}
