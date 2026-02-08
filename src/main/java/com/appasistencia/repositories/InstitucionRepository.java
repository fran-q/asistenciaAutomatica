package com.appasistencia.repositories;

import com.appasistencia.models.Institucion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstitucionRepository extends JpaRepository<Institucion, Long> {
    List<Institucion> findByActivoTrue();
}
