package com.appasistencia.repositories;

import com.appasistencia.models.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// Repositorio: acceso a datos de materias
@Repository
public interface MateriaRepository extends JpaRepository<Materia, Long> {
    // Retorna solo materias activas
    List<Materia> findByActivoTrue();
    // Filtra materias activas por carrera
    List<Materia> findByCarreraIdCarreraAndActivoTrue(Long idCarrera);
    // Busca materias activas por institucion (navega carrera -> institucion)
    @Query("SELECT m FROM Materia m WHERE m.carrera.institucion.idInstitucion = :idInst AND m.activo = true")
    List<Materia> findByInstitucion(@Param("idInst") Long idInstitucion);
}
