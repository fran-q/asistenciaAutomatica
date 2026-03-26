package com.appasistencia.repositories;

import com.appasistencia.models.UsuarioAlumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Repositorio: acceso a datos del perfil alumno (vinculado a Usuario)
@Repository
public interface UsuarioAlumnoRepository extends JpaRepository<UsuarioAlumno, Long> {
    // Retorna solo alumnos activos
    List<UsuarioAlumno> findByActivoTrue();
    // Busca alumno por legajo (validacion de unicidad)
    Optional<UsuarioAlumno> findByLegajo(String legajo);
    // Busca perfil alumno por id de usuario asociado
    Optional<UsuarioAlumno> findByUsuarioIdUsuario(Long idUsuario);
    // Busca alumnos activos por institucion (navega usuario -> institucion)
    @Query("SELECT a FROM UsuarioAlumno a WHERE a.usuario.institucion.idInstitucion = :idInst AND a.activo = true")
    List<UsuarioAlumno> findByInstitucion(@Param("idInst") Long idInstitucion);
}
