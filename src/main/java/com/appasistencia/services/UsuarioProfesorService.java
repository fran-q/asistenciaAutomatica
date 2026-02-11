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

@Service
@Transactional
public class UsuarioProfesorService {

    private final UsuarioProfesorRepository profesorRepository;
    private final UsuarioRepository usuarioRepository;

    public UsuarioProfesorService(UsuarioProfesorRepository profesorRepository, UsuarioRepository usuarioRepository) {
        this.profesorRepository = profesorRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<UsuarioProfesorResponseDTO> listarTodos() {
        return profesorRepository.findByActivoTrue().stream()
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

    public UsuarioProfesorResponseDTO actualizar(Long id, UsuarioProfesorDTO dto) {
        UsuarioProfesor profesor = buscarPorId(id);
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

    public void eliminar(Long id) {
        UsuarioProfesor profesor = buscarPorId(id);
        profesor.setActivo(false);
        profesorRepository.save(profesor);
    }
}
