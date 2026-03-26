package com.appasistencia.repositories;

import com.appasistencia.models.Institucion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Repositorio: acceso a datos de instituciones educativas
@Repository
public interface InstitucionRepository extends JpaRepository<Institucion, Long> {
    // Retorna solo instituciones con activo = true
    List<Institucion> findByActivoTrue();
    // Buscar por email (para validacion cruzada de unicidad)
    Optional<Institucion> findByEmail(String email);
}
