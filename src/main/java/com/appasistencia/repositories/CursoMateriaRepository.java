package com.appasistencia.repositories;

import com.appasistencia.models.CursoMateria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// Repositorio: acceso a datos de la relacion curso-materia
@Repository
public interface CursoMateriaRepository extends JpaRepository<CursoMateria, Long> {
    // Retorna solo relaciones curso-materia activas
    List<CursoMateria> findByActivoTrue();
    // Filtra materias asignadas a un curso
    List<CursoMateria> findByCursoIdCursoAndActivoTrue(Long idCurso);
    // Filtra cursos que tienen una materia especifica
    List<CursoMateria> findByMateriaIdMateriaAndActivoTrue(Long idMateria);
    // Busca relaciones activas por institucion (navega curso -> carrera -> institucion)
    @Query("SELECT cm FROM CursoMateria cm WHERE cm.curso.carrera.institucion.idInstitucion = :idInst AND cm.activo = true")
    List<CursoMateria> findByInstitucion(@Param("idInst") Long idInstitucion);
}
