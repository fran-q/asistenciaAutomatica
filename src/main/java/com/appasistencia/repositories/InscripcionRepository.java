package com.appasistencia.repositories;

import com.appasistencia.models.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {
    List<Inscripcion> findByActivoTrue();
    List<Inscripcion> findByAlumnoIdAlumnoAndActivoTrue(Long idAlumno);
    List<Inscripcion> findByCursoIdCursoAndActivoTrue(Long idCurso);
}
