package com.appasistencia.services;

import com.appasistencia.dtos.AsignacionDTO;
import com.appasistencia.dtos.response.AsignacionResponseDTO;
import com.appasistencia.exceptions.RecursoNoEncontradoException;
import com.appasistencia.models.Asignacion;
import com.appasistencia.models.CursoMateria;
import com.appasistencia.models.UsuarioProfesor;
import com.appasistencia.repositories.AsignacionRepository;
import com.appasistencia.repositories.CursoMateriaRepository;
import com.appasistencia.repositories.UsuarioProfesorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// Servicio: logica de negocio para cargos de profesor
@Service
@Transactional
public class AsignacionService {

    private final AsignacionRepository asignacionRepository;
    private final UsuarioProfesorRepository profesorRepository;
    private final CursoMateriaRepository cursoMateriaRepository;

    public AsignacionService(AsignacionRepository asignacionRepository,
                              UsuarioProfesorRepository profesorRepository,
                              CursoMateriaRepository cursoMateriaRepository) {
        this.asignacionRepository = asignacionRepository;
        this.profesorRepository = profesorRepository;
        this.cursoMateriaRepository = cursoMateriaRepository;
    }

    @Transactional(readOnly = true)
    public List<AsignacionResponseDTO> listarTodas() {
        return asignacionRepository.findAll().stream()
                .map(AsignacionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Listar filtrado por institucion
    @Transactional(readOnly = true)
    public List<AsignacionResponseDTO> listarTodas(Long idInstitucion) {
        return asignacionRepository.findByInstitucion(idInstitucion).stream()
                .map(AsignacionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AsignacionResponseDTO obtenerPorId(Long id) {
        return AsignacionResponseDTO.fromEntity(buscarPorId(id));
    }

    @Transactional(readOnly = true)
    public Asignacion buscarPorId(Long id) {
        return asignacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Asignacion", id));
    }

    @Transactional(readOnly = true)
    public List<AsignacionResponseDTO> listarPorProfesor(Long idProfesor) {
        return asignacionRepository.findByProfesorIdProfesorAndActivoTrue(idProfesor).stream()
                .map(AsignacionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Crear validando que profesor y cursoMateria existan
    public AsignacionResponseDTO crear(AsignacionDTO dto) {
        UsuarioProfesor profesor = profesorRepository.findById(dto.getIdProfesor())
                .orElseThrow(() -> new RecursoNoEncontradoException("Profesor", dto.getIdProfesor()));
        CursoMateria cursoMateria = cursoMateriaRepository.findById(dto.getIdCursoMateria())
                .orElseThrow(() -> new RecursoNoEncontradoException("CursoMateria", dto.getIdCursoMateria()));

        Asignacion asignacion = new Asignacion(profesor, cursoMateria);
        return AsignacionResponseDTO.fromEntity(asignacionRepository.save(asignacion));
    }

    // Eliminar (borrado logico)
    public void eliminar(Long id) {
        Asignacion asignacion = buscarPorId(id);
        asignacion.setActivo(false);
        asignacionRepository.save(asignacion);
    }

    public void reactivar(Long id) {
        Asignacion asignacion = buscarPorId(id);
        asignacion.setActivo(true);
        asignacionRepository.save(asignacion);
    }

    // === Metodos con validacion de institucion ===
    // Estos metodos verifican que el recurso pertenezca a la institucion del usuario autenticado

    // Verifica que el recurso pertenezca a la institucion del usuario autenticado
    private void verificarInstitucion(Long idInstitucionRecurso, Long idInstitucionUsuario) {
        if (!idInstitucionRecurso.equals(idInstitucionUsuario)) {
            throw new RecursoNoEncontradoException("Asignacion", 0L);
        }
    }

    // Obtener asignacion por ID validando que pertenece a la misma institucion
    @Transactional(readOnly = true)
    public AsignacionResponseDTO obtenerPorId(Long id, Long idInstitucion) {
        Asignacion asignacion = buscarPorId(id);
        verificarInstitucion(asignacion.getCursoMateria().getCurso().getCarrera().getInstitucion().getIdInstitucion(), idInstitucion);
        return AsignacionResponseDTO.fromEntity(asignacion);
    }

    // Eliminar asignacion validando que pertenece a la misma institucion
    public void eliminar(Long id, Long idInstitucion) {
        Asignacion asignacion = buscarPorId(id);
        verificarInstitucion(asignacion.getCursoMateria().getCurso().getCarrera().getInstitucion().getIdInstitucion(), idInstitucion);
        eliminar(id);
    }

    // Reactivar asignacion validando que pertenece a la misma institucion
    public void reactivar(Long id, Long idInstitucion) {
        Asignacion asignacion = buscarPorId(id);
        verificarInstitucion(asignacion.getCursoMateria().getCurso().getCarrera().getInstitucion().getIdInstitucion(), idInstitucion);
        reactivar(id);
    }
}
