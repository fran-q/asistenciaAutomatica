package com.appasistencia.repositories;

import com.appasistencia.models.Usuario;
import com.appasistencia.models.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    List<Usuario> findByActivoTrue();
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByNumeroDocumento(String numeroDocumento);
    List<Usuario> findByRolAndActivoTrue(Rol rol);
}
