package com.appasistencia.repositories;

import com.appasistencia.models.Notificacion;
import com.appasistencia.models.TipoNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Repositorio: acceso a datos de notificaciones del sistema
@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    // Retorna solo notificaciones activas
    List<Notificacion> findByActivoTrue();
    // Filtra notificaciones activas de un alumno
    List<Notificacion> findByAlumnoIdAlumnoAndActivoTrue(Long idAlumno);
    // Filtra notificaciones activas por tipo (AUSENCIA, TARDANZA, etc.)
    List<Notificacion> findByTipoAndActivoTrue(TipoNotificacion tipo);
}
