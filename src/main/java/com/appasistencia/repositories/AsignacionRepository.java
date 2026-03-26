package com.appasistencia.repositories;

import com.appasistencia.models.Asignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// Repositorio: acceso a datos de asignaciones (profesor a curso-materia)
@Repository
public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {
    // Retorna solo asignaciones activas
    List<Asignacion> findByActivoTrue();
    // Filtra asignaciones activas de un profesor
    List<Asignacion> findByProfesorIdProfesorAndActivoTrue(Long idProfesor);
    // Filtra asignaciones activas de un curso-materia
    List<Asignacion> findByCursoMateriaIdCursoMateriaAndActivoTrue(Long idCursoMateria);
    // Busca asignaciones activas por institucion (navega cursoMateria -> curso -> carrera -> institucion)
    @Query("SELECT a FROM Asignacion a WHERE a.cursoMateria.curso.carrera.institucion.idInstitucion = :idInst AND a.activo = true")
    List<Asignacion> findByInstitucion(@Param("idInst") Long idInstitucion);
}
