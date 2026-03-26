package com.appasistencia.repositories;

import com.appasistencia.models.PlantillaBiometrica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// Repositorio: acceso a datos de plantillas biometricas (rostros enrolados)
@Repository
public interface PlantillaBiometricaRepository extends JpaRepository<PlantillaBiometrica, Long> {
    // Retorna solo plantillas activas
    List<PlantillaBiometrica> findByActivoTrue();
    // Filtra plantillas activas de un usuario
    List<PlantillaBiometrica> findByUsuarioIdUsuarioAndActivoTrue(Long idUsuario);
    // Busca plantillas activas por institucion (navega usuario -> institucion)
    @Query("SELECT p FROM PlantillaBiometrica p WHERE p.usuario.institucion.idInstitucion = :idInst AND p.activo = true")
    List<PlantillaBiometrica> findByInstitucion(@Param("idInst") Long idInstitucion);
}
