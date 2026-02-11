package com.appasistencia.services;

import com.appasistencia.dtos.CursoMateriaDTO;
import com.appasistencia.dtos.response.CursoMateriaResponseDTO;
import com.appasistencia.exceptions.RecursoNoEncontradoException;
import com.appasistencia.models.Curso;
import com.appasistencia.models.CursoMateria;
import com.appasistencia.models.Materia;
import com.appasistencia.repositories.CursoMateriaRepository;
import com.appasistencia.repositories.CursoRepository;
import com.appasistencia.repositories.MateriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CursoMateriaService {

    private final CursoMateriaRepository cursoMateriaRepository;
    private final CursoRepository cursoRepository;
    private final MateriaRepository materiaRepository;

    public CursoMateriaService(CursoMateriaRepository cursoMateriaRepository,
                                CursoRepository cursoRepository, MateriaRepository materiaRepository) {
        this.cursoMateriaRepository = cursoMateriaRepository;
        this.cursoRepository = cursoRepository;
        this.materiaRepository = materiaRepository;
    }

    @Transactional(readOnly = true)
    public List<CursoMateriaResponseDTO> listarTodos() {
        return cursoMateriaRepository.findByActivoTrue().stream()
                .map(CursoMateriaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CursoMateriaResponseDTO obtenerPorId(Long id) {
        return CursoMateriaResponseDTO.fromEntity(buscarPorId(id));
    }

    @Transactional(readOnly = true)
    public CursoMateria buscarPorId(Long id) {
        return cursoMateriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("CursoMateria", id));
    }

    @Transactional(readOnly = true)
    public List<CursoMateriaResponseDTO> listarPorCurso(Long idCurso) {
        return cursoMateriaRepository.findByCursoIdCursoAndActivoTrue(idCurso).stream()
                .map(CursoMateriaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public CursoMateriaResponseDTO crear(CursoMateriaDTO dto) {
        Curso curso = cursoRepository.findById(dto.getIdCurso())
                .orElseThrow(() -> new RecursoNoEncontradoException("Curso", dto.getIdCurso()));
        Materia materia = materiaRepository.findById(dto.getIdMateria())
                .orElseThrow(() -> new RecursoNoEncontradoException("Materia", dto.getIdMateria()));

        CursoMateria cm = new CursoMateria(curso, materia);
        return CursoMateriaResponseDTO.fromEntity(cursoMateriaRepository.save(cm));
    }

    public void eliminar(Long id) {
        CursoMateria cm = buscarPorId(id);
        cm.setActivo(false);
        cursoMateriaRepository.save(cm);
    }
}
