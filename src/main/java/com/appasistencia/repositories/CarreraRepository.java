package com.appasistencia.repositories;

import com.appasistencia.models.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Repositorio: acceso a datos de carreras
@Repository
public interface CarreraRepository extends JpaRepository<Carrera, Long> {
    // Retorna solo carreras activas
    List<Carrera> findByActivoTrue();
    // Filtra carreras activas por institucion
    List<Carrera> findByInstitucionIdInstitucionAndActivoTrue(Long idInstitucion);
    // Filtra todas las carreras por institucion (incluye inactivas)
    List<Carrera> findByInstitucionIdInstitucion(Long idInstitucion);
}
