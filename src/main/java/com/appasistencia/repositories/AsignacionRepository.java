package com.appasistencia.repositories;

import com.appasistencia.models.Asignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {
    List<Asignacion> findByActivoTrue();
    List<Asignacion> findByProfesorIdProfesorAndActivoTrue(Long idProfesor);
    List<Asignacion> findByCursoMateriaIdCursoMateriaAndActivoTrue(Long idCursoMateria);
}
