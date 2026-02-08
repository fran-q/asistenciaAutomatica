package com.appasistencia.repositories;

import com.appasistencia.models.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarreraRepository extends JpaRepository<Carrera, Long> {
    List<Carrera> findByActivoTrue();
    List<Carrera> findByInstitucionIdInstitucionAndActivoTrue(Long idInstitucion);
}
