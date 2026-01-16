package com.appasistencia.repositories;

import com.appasistencia.models.Asistencia;
import com.appasistencia.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Integer> {
    List<Asistencia> findByUsuario(Usuario usuario);
}