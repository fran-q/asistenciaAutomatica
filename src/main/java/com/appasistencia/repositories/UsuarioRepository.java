package com.appasistencia.repositories;

import com.appasistencia.models.Usuario;
import com.appasistencia.models.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Repositorio: acceso a datos de usuarios del sistema
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Retorna solo usuarios activos
    List<Usuario> findByActivoTrue();
    // Busca usuario por email (login)
    Optional<Usuario> findByEmail(String email);
    // Busca usuario por DNI (validacion de unicidad)
    Optional<Usuario> findByNumeroDocumento(String numeroDocumento);
    // Filtra usuarios activos por rol (ADMIN, PROFESOR, ALUMNO)
    List<Usuario> findByRolAndActivoTrue(Rol rol);
    // Filtra usuarios por rol e institucion
    List<Usuario> findByRolAndInstitucionIdInstitucion(Rol rol, Long idInstitucion);
    // Retorna todos los usuarios de una institucion
    List<Usuario> findByInstitucionIdInstitucion(Long idInstitucion);
}
