package com.appasistencia.repositories;

import com.appasistencia.models.UsuarioProfesor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Repositorio: acceso a datos del perfil profesor (vinculado a Usuario)
@Repository
public interface UsuarioProfesorRepository extends JpaRepository<UsuarioProfesor, Long> {
    // Retorna solo profesores activos
    List<UsuarioProfesor> findByActivoTrue();
    // Busca profesor por legajo (validacion de unicidad)
    Optional<UsuarioProfesor> findByLegajo(String legajo);
    // Busca perfil profesor por id de usuario asociado
    Optional<UsuarioProfesor> findByUsuarioIdUsuario(Long idUsuario);
    // Busca profesores activos por institucion (navega usuario -> institucion)
    @Query("SELECT p FROM UsuarioProfesor p WHERE p.usuario.institucion.idInstitucion = :idInst AND p.activo = true")
    List<UsuarioProfesor> findByInstitucion(@Param("idInst") Long idInstitucion);
}
