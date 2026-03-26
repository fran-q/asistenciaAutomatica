package com.appasistencia.repositories;

import com.appasistencia.models.CodigoVerificacion;
import com.appasistencia.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Repositorio: acceso a codigos de verificacion de email
@Repository
public interface CodigoVerificacionRepository extends JpaRepository<CodigoVerificacion, Long> {
    Optional<CodigoVerificacion> findByUsuario(Usuario usuario);
    Optional<CodigoVerificacion> findByUsuarioIdUsuario(Long idUsuario);
    void deleteByUsuario(Usuario usuario);
}
