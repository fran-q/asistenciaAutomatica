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

// Servicio: logica de negocio para relacion curso-materia
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
        return cursoMateriaRepository.findAll().stream()
                .map(CursoMateriaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Listar filtrado por institucion
    @Transactional(readOnly = true)
    public List<CursoMateriaResponseDTO> listarTodos(Long idInstitucion) {
        return cursoMateriaRepository.findByInstitucion(idInstitucion).stream()
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

    // Crear validando que curso y materia existan
    public CursoMateriaResponseDTO crear(CursoMateriaDTO dto) {
        Curso curso = cursoRepository.findById(dto.getIdCurso())
                .orElseThrow(() -> new RecursoNoEncontradoException("Curso", dto.getIdCurso()));
        Materia materia = materiaRepository.findById(dto.getIdMateria())
                .orElseThrow(() -> new RecursoNoEncontradoException("Materia", dto.getIdMateria()));

        CursoMateria cm = new CursoMateria(curso, materia);
        return CursoMateriaResponseDTO.fromEntity(cursoMateriaRepository.save(cm));
    }

    // Eliminar (borrado logico)
    public void eliminar(Long id) {
        CursoMateria cm = buscarPorId(id);
        cm.setActivo(false);
        cursoMateriaRepository.save(cm);
    }

    public void reactivar(Long id) {
        CursoMateria cm = buscarPorId(id);
        cm.setActivo(true);
        cursoMateriaRepository.save(cm);
    }

    // === Metodos con validacion de institucion ===
    // Estos metodos verifican que el recurso pertenezca a la institucion del usuario autenticado

    // Verifica que el recurso pertenezca a la institucion del usuario autenticado
    private void verificarInstitucion(Long idInstitucionRecurso, Long idInstitucionUsuario) {
        if (!idInstitucionRecurso.equals(idInstitucionUsuario)) {
            throw new RecursoNoEncontradoException("CursoMateria", 0L);
        }
    }

    // Obtener curso-materia por ID validando que pertenece a la misma institucion
    @Transactional(readOnly = true)
    public CursoMateriaResponseDTO obtenerPorId(Long id, Long idInstitucion) {
        CursoMateria cm = buscarPorId(id);
        verificarInstitucion(cm.getCurso().getCarrera().getInstitucion().getIdInstitucion(), idInstitucion);
        return CursoMateriaResponseDTO.fromEntity(cm);
    }

    // Eliminar curso-materia validando que pertenece a la misma institucion
    public void eliminar(Long id, Long idInstitucion) {
        CursoMateria cm = buscarPorId(id);
        verificarInstitucion(cm.getCurso().getCarrera().getInstitucion().getIdInstitucion(), idInstitucion);
        eliminar(id);
    }

    // Reactivar curso-materia validando que pertenece a la misma institucion
    public void reactivar(Long id, Long idInstitucion) {
        CursoMateria cm = buscarPorId(id);
        verificarInstitucion(cm.getCurso().getCarrera().getInstitucion().getIdInstitucion(), idInstitucion);
        reactivar(id);
    }
}
