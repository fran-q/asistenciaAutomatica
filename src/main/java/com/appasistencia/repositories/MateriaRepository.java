package com.appasistencia.repositories;

import com.appasistencia.models.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MateriaRepository extends JpaRepository<Materia, Long> {
    List<Materia> findByActivoTrue();
    List<Materia> findByCarreraIdCarreraAndActivoTrue(Long idCarrera);
}
