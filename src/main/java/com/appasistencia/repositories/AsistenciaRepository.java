package com.appasistencia.repositories;

import com.appasistencia.models.Asistencia;
import com.appasistencia.models.EstadoAsistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

// Repositorio: acceso a datos de registros de asistencia
@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {
    // Retorna solo asistencias activas
    List<Asistencia> findByActivoTrue();
    // Filtra asistencias activas de un profesor
    List<Asistencia> findByProfesorIdProfesorAndActivoTrue(Long idProfesor);
    // Filtra asistencias activas de una asignacion
    List<Asistencia> findByAsignacionIdAsignacionAndActivoTrue(Long idAsignacion);
    // Filtra asistencias activas por fecha exacta
    List<Asistencia> findByFechaAndActivoTrue(LocalDate fecha);
    // Filtra asistencias activas en un rango de fechas
    List<Asistencia> findByFechaBetweenAndActivoTrue(LocalDate desde, LocalDate hasta);
    // Filtra asistencias activas por estado (PRESENTE, AUSENTE, TARDANZA)
    List<Asistencia> findByEstadoAndActivoTrue(EstadoAsistencia estado);
    // Busca asistencias activas por institucion (navega profesor -> usuario -> institucion)
    @Query("SELECT a FROM Asistencia a WHERE a.profesor.usuario.institucion.idInstitucion = :idInst AND a.activo = true")
    List<Asistencia> findByInstitucion(@Param("idInst") Long idInstitucion);

    // Verifica si ya existe asistencia de un profesor en una asignacion y fecha (evita duplicados)
    boolean existsByProfesorIdProfesorAndAsignacionIdAsignacionAndFechaAndActivoTrue(
            Long idProfesor, Long idAsignacion, LocalDate fecha);
}
