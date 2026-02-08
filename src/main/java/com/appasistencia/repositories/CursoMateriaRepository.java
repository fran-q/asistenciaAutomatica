package com.appasistencia.repositories;

import com.appasistencia.models.CursoMateria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CursoMateriaRepository extends JpaRepository<CursoMateria, Long> {
    List<CursoMateria> findByActivoTrue();
    List<CursoMateria> findByCursoIdCursoAndActivoTrue(Long idCurso);
    List<CursoMateria> findByMateriaIdMateriaAndActivoTrue(Long idMateria);
}
