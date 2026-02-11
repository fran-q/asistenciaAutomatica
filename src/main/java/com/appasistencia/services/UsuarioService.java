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

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findByActivoTrue().stream()
                .map(UsuarioResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerPorId(Long id) {
        Usuario usuario = buscarPorId(id);
        return UsuarioResponseDTO.fromEntity(usuario);
    }

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

    public UsuarioResponseDTO crear(UsuarioDTO dto) {
        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RecursoDuplicadoException("Ya existe un usuario con el email: " + dto.getEmail());
        }
        if (usuarioRepository.findByNumeroDocumento(dto.getNumeroDocumento()).isPresent()) {
            throw new RecursoDuplicadoException("Ya existe un usuario con el documento: " + dto.getNumeroDocumento());
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

    public UsuarioResponseDTO actualizar(Long id, UsuarioDTO dto) {
        Usuario usuario = buscarPorId(id);

        // Validar unicidad de email si cambió
        if (!usuario.getEmail().equals(dto.getEmail())) {
            Optional<Usuario> existente = usuarioRepository.findByEmail(dto.getEmail());
            if (existente.isPresent() && !existente.get().getIdUsuario().equals(id)) {
                throw new RecursoDuplicadoException("Ya existe un usuario con el email: " + dto.getEmail());
            }
        }

        // Validar unicidad de documento si cambió
        if (!usuario.getNumeroDocumento().equals(dto.getNumeroDocumento())) {
            Optional<Usuario> existente = usuarioRepository.findByNumeroDocumento(dto.getNumeroDocumento());
            if (existente.isPresent() && !existente.get().getIdUsuario().equals(id)) {
                throw new RecursoDuplicadoException("Ya existe un usuario con el documento: " + dto.getNumeroDocumento());
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

    public void eliminar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", id));
    }
}
