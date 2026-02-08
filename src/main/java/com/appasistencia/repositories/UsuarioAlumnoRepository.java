package com.appasistencia.repositories;

import com.appasistencia.models.UsuarioAlumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioAlumnoRepository extends JpaRepository<UsuarioAlumno, Long> {
    List<UsuarioAlumno> findByActivoTrue();
    Optional<UsuarioAlumno> findByLegajo(String legajo);
    Optional<UsuarioAlumno> findByUsuarioIdUsuario(Long idUsuario);
}
