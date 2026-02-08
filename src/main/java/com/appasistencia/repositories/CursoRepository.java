package com.appasistencia.repositories;

import com.appasistencia.models.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {
    List<Curso> findByActivoTrue();
    List<Curso> findByCarreraIdCarreraAndActivoTrue(Long idCarrera);
    List<Curso> findByAnioLectivoAndActivoTrue(int anioLectivo);
}
