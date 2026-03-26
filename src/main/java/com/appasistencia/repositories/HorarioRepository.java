package com.appasistencia.repositories;

import com.appasistencia.models.DiaSemana;
import com.appasistencia.models.Horario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

// Repositorio: acceso a datos de horarios de clase
@Repository
public interface HorarioRepository extends JpaRepository<Horario, Long> {
    // Retorna solo horarios activos
    List<Horario> findByActivoTrue();
    // Filtra horarios activos de una asignacion
    List<Horario> findByAsignacionIdAsignacionAndActivoTrue(Long idAsignacion);
    // Filtra horarios activos por dia de la semana
    List<Horario> findByDiaSemanaAndActivoTrue(DiaSemana diaSemana);
    // Busca horarios activos por institucion (navega asignacion -> cursoMateria -> curso -> carrera -> institucion)
    @Query("SELECT h FROM Horario h WHERE h.asignacion.cursoMateria.curso.carrera.institucion.idInstitucion = :idInst AND h.activo = true")
    List<Horario> findByInstitucion(@Param("idInst") Long idInstitucion);

    // Busca horarios activos de un profesor en un dia y hora especificos (para verificar clase en curso)
    @Query("SELECT h FROM Horario h WHERE h.diaSemana = :dia " +
           "AND h.horaInicio <= :hora AND h.horaFin >= :hora " +
           "AND h.asignacion.profesor.idProfesor = :idProfesor " +
           "AND h.asignacion.activo = true AND h.activo = true")
    List<Horario> findActiveByDiaAndHoraAndProfesor(
            @Param("dia") DiaSemana dia,
            @Param("hora") LocalTime hora,
            @Param("idProfesor") Long idProfesor);
}
