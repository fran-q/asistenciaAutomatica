package com.appasistencia.services;

import com.appasistencia.dtos.InscripcionDTO;
import com.appasistencia.dtos.response.InscripcionResponseDTO;
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
        return inscripcionRepository.findByActivoTrue().stream()
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

    public InscripcionResponseDTO crear(InscripcionDTO dto) {
        UsuarioAlumno alumno = alumnoRepository.findById(dto.getIdAlumno())
                .orElseThrow(() -> new RecursoNoEncontradoException("Alumno", dto.getIdAlumno()));
        Curso curso = cursoRepository.findById(dto.getIdCurso())
                .orElseThrow(() -> new RecursoNoEncontradoException("Curso", dto.getIdCurso()));

        Inscripcion inscripcion = new Inscripcion(alumno, curso);
        return InscripcionResponseDTO.fromEntity(inscripcionRepository.save(inscripcion));
    }

    public InscripcionResponseDTO actualizar(Long id, InscripcionDTO dto) {
        Inscripcion inscripcion = buscarPorId(id);

        UsuarioAlumno alumno = alumnoRepository.findById(dto.getIdAlumno())
                .orElseThrow(() -> new RecursoNoEncontradoException("Alumno", dto.getIdAlumno()));
        Curso curso = cursoRepository.findById(dto.getIdCurso())
                .orElseThrow(() -> new RecursoNoEncontradoException("Curso", dto.getIdCurso()));

        inscripcion.setAlumno(alumno);
        inscripcion.setCurso(curso);

        return InscripcionResponseDTO.fromEntity(inscripcionRepository.save(inscripcion));
    }

    public void eliminar(Long id) {
        Inscripcion inscripcion = buscarPorId(id);
        inscripcion.setActivo(false);
        inscripcionRepository.save(inscripcion);
    }
}
