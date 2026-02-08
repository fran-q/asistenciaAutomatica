package com.appasistencia.repositories;

import com.appasistencia.models.Notificacion;
import com.appasistencia.models.TipoNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByActivoTrue();
    List<Notificacion> findByAlumnoIdAlumnoAndActivoTrue(Long idAlumno);
    List<Notificacion> findByTipoAndActivoTrue(TipoNotificacion tipo);
}
