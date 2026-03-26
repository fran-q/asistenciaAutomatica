package com.appasistencia.repositories;

import com.appasistencia.models.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// Repositorio: acceso a datos de cursos
@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {
    // Retorna solo cursos activos
    List<Curso> findByActivoTrue();
    // Filtra cursos activos por carrera
    List<Curso> findByCarreraIdCarreraAndActivoTrue(Long idCarrera);
    // Filtra cursos activos por anio lectivo
    List<Curso> findByAnioLectivoAndActivoTrue(int anioLectivo);
    // Busca cursos activos por institucion (navega carrera -> institucion)
    @Query("SELECT c FROM Curso c WHERE c.carrera.institucion.idInstitucion = :idInst AND c.activo = true")
    List<Curso> findByInstitucion(@Param("idInst") Long idInstitucion);
}
