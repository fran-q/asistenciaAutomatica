package com.appasistencia.repositories;

import com.appasistencia.models.UsuarioProfesor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioProfesorRepository extends JpaRepository<UsuarioProfesor, Long> {
    List<UsuarioProfesor> findByActivoTrue();
    Optional<UsuarioProfesor> findByLegajo(String legajo);
    Optional<UsuarioProfesor> findByUsuarioIdUsuario(Long idUsuario);
}
