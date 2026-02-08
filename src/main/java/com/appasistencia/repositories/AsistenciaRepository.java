package com.appasistencia.repositories;

import com.appasistencia.models.Asistencia;
import com.appasistencia.models.EstadoAsistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {
    List<Asistencia> findByActivoTrue();
    List<Asistencia> findByProfesorIdProfesorAndActivoTrue(Long idProfesor);
    List<Asistencia> findByAsignacionIdAsignacionAndActivoTrue(Long idAsignacion);
    List<Asistencia> findByFechaAndActivoTrue(LocalDate fecha);
    List<Asistencia> findByFechaBetweenAndActivoTrue(LocalDate desde, LocalDate hasta);
    List<Asistencia> findByEstadoAndActivoTrue(EstadoAsistencia estado);
}
