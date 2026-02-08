package com.appasistencia.controllers;

import com.appasistencia.dtos.NotificacionDTO;
import com.appasistencia.models.Notificacion;
import com.appasistencia.models.TipoNotificacion;
import com.appasistencia.repositories.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final NotificacionRepository notificacionRepository;
    private final UsuarioAlumnoRepository alumnoRepository;
    private final AsistenciaRepository asistenciaRepository;
    private final AsignacionRepository asignacionRepository;

    public NotificacionController(NotificacionRepository notificacionRepository,
                                   UsuarioAlumnoRepository alumnoRepository,
                                   AsistenciaRepository asistenciaRepository,
                                   AsignacionRepository asignacionRepository) {
        this.notificacionRepository = notificacionRepository;
        this.alumnoRepository = alumnoRepository;
        this.asistenciaRepository = asistenciaRepository;
        this.asignacionRepository = asignacionRepository;
    }

    @GetMapping
    public List<Notificacion> listarTodas() {
        return notificacionRepository.findByActivoTrue();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notificacion> obtenerPorId(@PathVariable Long id) {
        return notificacionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/alumno/{idAlumno}")
    public List<Notificacion> listarPorAlumno(@PathVariable Long idAlumno) {
        return notificacionRepository.findByAlumnoIdAlumnoAndActivoTrue(idAlumno);
    }

    @PostMapping
    public ResponseEntity<Notificacion> crear(@RequestBody NotificacionDTO dto) {
        var alumnoOpt = alumnoRepository.findById(dto.getIdAlumno());
        if (alumnoOpt.isEmpty()) return ResponseEntity.badRequest().build();

        Notificacion notificacion = new Notificacion();
        notificacion.setAlumno(alumnoOpt.get());
        notificacion.setTipo(TipoNotificacion.valueOf(dto.getTipo()));
        notificacion.setTitulo(dto.getTitulo());
        notificacion.setMensaje(dto.getMensaje());
        notificacion.setFechaCreacion(java.time.LocalDateTime.now());

        if (dto.getIdAsistencia() != null) {
            asistenciaRepository.findById(dto.getIdAsistencia())
                    .ifPresent(notificacion::setAsistencia);
        }
        if (dto.getIdAsignacion() != null) {
            asignacionRepository.findById(dto.getIdAsignacion())
                    .ifPresent(notificacion::setAsignacion);
        }

        return ResponseEntity.ok(notificacionRepository.save(notificacion));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return notificacionRepository.findById(id).map(notificacion -> {
            notificacion.setActivo(false);
            notificacionRepository.save(notificacion);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
