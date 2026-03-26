package com.appasistencia.services;

import com.appasistencia.dtos.InscripcionDTO;
import com.appasistencia.dtos.response.InscripcionResponseDTO;
import com.appasistencia.exceptions.RecursoDuplicadoException;
import com.appasistencia.exceptions.RecursoNoEncontradoException;
import com.appasistencia.models.Curso;
import com.appasistencia.models.Inscripcion;
import com.appasistencia.models.UsuarioAlumno;
import com.appasistencia.repositories.CursoRepository;
import com.appasistencia.repositories.InscripcionRepository;
import com.appasistencia.repositories.UsuarioAlumnoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// Servicio: logica de negocio para inscripciones de alumnos
@Service
@Transactional
public class InscripcionService {

    private final InscripcionRepository inscripcionRepository;
    private final UsuarioAlumnoRepository alumnoRepository;
    private final CursoRepository cursoRepository;

    public InscripcionService(InscripcionRepository inscripcionRepository,
                               UsuarioAlumnoRepository alumnoRepository, CursoRepository cursoRepository) {
        this.inscripcionRepository = inscripcionRepository;
        this.alumnoRepository = alumnoRepository;
        this.cursoRepository = cursoRepository;
    }

    @Transactional(readOnly = true)
    public List<InscripcionResponseDTO> listarTodas() {
        return inscripcionRepository.findAll().stream()
                .map(InscripcionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Listar filtrado por institucion
    @Transactional(readOnly = true)
    public List<InscripcionResponseDTO> listarTodas(Long idInstitucion) {
        return inscripcionRepository.findByInstitucion(idInstitucion).stream()
                .map(InscripcionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InscripcionResponseDTO obtenerPorId(Long id) {
        return InscripcionResponseDTO.fromEntity(buscarPorId(id));
    }

    @Transactional(readOnly = true)
    public Inscripcion buscarPorId(Long id) {
        return inscripcionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Inscripcion", id));
    }

    @Transactional(readOnly = true)
    public List<InscripcionResponseDTO> listarPorAlumno(Long idAlumno) {
        return inscripcionRepository.findByAlumnoIdAlumnoAndActivoTrue(idAlumno).stream()
                .map(InscripcionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InscripcionResponseDTO> listarPorCurso(Long idCurso) {
        return inscripcionRepository.findByCursoIdCursoAndActivoTrue(idCurso).stream()
                .map(InscripcionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Crear validando que alumno y curso existan y no haya inscripcion duplicada activa
    public InscripcionResponseDTO crear(InscripcionDTO dto) {
        UsuarioAlumno alumno = alumnoRepository.findById(dto.getIdAlumno())
                .orElseThrow(() -> new RecursoNoEncontradoException("Alumno", dto.getIdAlumno()));
        Curso curso = cursoRepository.findById(dto.getIdCurso())
                .orElseThrow(() -> new RecursoNoEncontradoException("Curso", dto.getIdCurso()));

        // Verificar que no exista una inscripcion activa del mismo alumno en el mismo curso
        if (inscripcionRepository.existsByAlumnoIdAlumnoAndCursoIdCursoAndActivoTrue(dto.getIdAlumno(), dto.getIdCurso())) {
            throw new RecursoDuplicadoException("El alumno ya esta inscripto en este curso");
        }

        Inscripcion inscripcion = new Inscripcion(alumno, curso);
        return InscripcionResponseDTO.fromEntity(inscripcionRepository.save(inscripcion));
    }

    // Eliminar (borrado logico)
    public void eliminar(Long id) {
        Inscripcion inscripcion = buscarPorId(id);
        inscripcion.setActivo(false);
        inscripcionRepository.save(inscripcion);
    }

    public void reactivar(Long id) {
        Inscripcion inscripcion = buscarPorId(id);
        inscripcion.setActivo(true);
        inscripcionRepository.save(inscripcion);
    }

    // === Metodos con validacion de institucion ===
    // Estos metodos verifican que el recurso pertenezca a la institucion del usuario autenticado

    // Verifica que el recurso pertenezca a la institucion del usuario autenticado
    private void verificarInstitucion(Long idInstitucionRecurso, Long idInstitucionUsuario) {
        if (!idInstitucionRecurso.equals(idInstitucionUsuario)) {
            throw new RecursoNoEncontradoException("Inscripcion", 0L);
        }
    }

    // Obtener inscripcion por ID validando que pertenece a la misma institucion
    @Transactional(readOnly = true)
    public InscripcionResponseDTO obtenerPorId(Long id, Long idInstitucion) {
        Inscripcion inscripcion = buscarPorId(id);
        verificarInstitucion(inscripcion.getCurso().getCarrera().getInstitucion().getIdInstitucion(), idInstitucion);
        return InscripcionResponseDTO.fromEntity(inscripcion);
    }

    // Eliminar inscripcion validando que pertenece a la misma institucion
    public void eliminar(Long id, Long idInstitucion) {
        Inscripcion inscripcion = buscarPorId(id);
        verificarInstitucion(inscripcion.getCurso().getCarrera().getInstitucion().getIdInstitucion(), idInstitucion);
        eliminar(id);
    }

    // Reactivar inscripcion validando que pertenece a la misma institucion
    public void reactivar(Long id, Long idInstitucion) {
        Inscripcion inscripcion = buscarPorId(id);
        verificarInstitucion(inscripcion.getCurso().getCarrera().getInstitucion().getIdInstitucion(), idInstitucion);
        reactivar(id);
    }
}
