package com.appasistencia.repositories;

import com.appasistencia.models.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// Repositorio: acceso a datos de inscripciones (alumno a curso)
@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {
    // Retorna solo inscripciones activas
    List<Inscripcion> findByActivoTrue();
    // Filtra inscripciones activas de un alumno
    List<Inscripcion> findByAlumnoIdAlumnoAndActivoTrue(Long idAlumno);
    // Filtra inscripciones activas de un curso
    List<Inscripcion> findByCursoIdCursoAndActivoTrue(Long idCurso);
    // Busca inscripciones activas por institucion (navega curso -> carrera -> institucion)
    @Query("SELECT i FROM Inscripcion i WHERE i.curso.carrera.institucion.idInstitucion = :idInst AND i.activo = true")
    List<Inscripcion> findByInstitucion(@Param("idInst") Long idInstitucion);

    // Verifica si un alumno ya esta inscripto en un curso (evita duplicados)
    boolean existsByAlumnoIdAlumnoAndCursoIdCursoAndActivoTrue(Long idAlumno, Long idCurso);
}
