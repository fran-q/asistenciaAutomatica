package com.appasistencia.services;

import com.appasistencia.dtos.NotificacionDTO;
import com.appasistencia.dtos.response.NotificacionResponseDTO;
import com.appasistencia.exceptions.RecursoNoEncontradoException;
import com.appasistencia.exceptions.OperacionInvalidaException;
import com.appasistencia.models.Notificacion;
import com.appasistencia.models.TipoNotificacion;
import com.appasistencia.models.UsuarioAlumno;
import com.appasistencia.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// Servicio: logica de negocio para notificaciones
@Service
@Transactional
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final UsuarioAlumnoRepository alumnoRepository;
    private final AsistenciaRepository asistenciaRepository;
    private final AsignacionRepository asignacionRepository;

    public NotificacionService(NotificacionRepository notificacionRepository,
                                UsuarioAlumnoRepository alumnoRepository,
                                AsistenciaRepository asistenciaRepository,
                                AsignacionRepository asignacionRepository) {
        this.notificacionRepository = notificacionRepository;
        this.alumnoRepository = alumnoRepository;
        this.asistenciaRepository = asistenciaRepository;
        this.asignacionRepository = asignacionRepository;
    }

    @Transactional(readOnly = true)
    public List<NotificacionResponseDTO> listarTodas() {
        return notificacionRepository.findByActivoTrue().stream()
                .map(NotificacionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public NotificacionResponseDTO obtenerPorId(Long id) {
        return NotificacionResponseDTO.fromEntity(buscarPorId(id));
    }

    @Transactional(readOnly = true)
    public Notificacion buscarPorId(Long id) {
        return notificacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Notificacion", id));
    }

    @Transactional(readOnly = true)
    public List<NotificacionResponseDTO> listarPorAlumno(Long idAlumno) {
        return notificacionRepository.findByAlumnoIdAlumnoAndActivoTrue(idAlumno).stream()
                .map(NotificacionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Crear validando tipo de notificacion y vinculando asistencia/asignacion opcionales
    public NotificacionResponseDTO crear(NotificacionDTO dto) {
        UsuarioAlumno alumno = alumnoRepository.findById(dto.getIdAlumno())
                .orElseThrow(() -> new RecursoNoEncontradoException("Alumno", dto.getIdAlumno()));

        TipoNotificacion tipo;
        try {
            tipo = TipoNotificacion.valueOf(dto.getTipo().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new OperacionInvalidaException("Tipo de notificacion invalido: " + dto.getTipo() + ". Valores permitidos: ASISTENCIA, INASISTENCIA, TARDANZA, GENERAL");
        }

        Notificacion notificacion = new Notificacion();
        notificacion.setAlumno(alumno);
        notificacion.setTipo(tipo);
        notificacion.setTitulo(dto.getTitulo());
        notificacion.setMensaje(dto.getMensaje());
        notificacion.setFechaCreacion(LocalDateTime.now());

        if (dto.getIdAsistencia() != null) {
            asistenciaRepository.findById(dto.getIdAsistencia())
                    .ifPresent(notificacion::setAsistencia);
        }
        if (dto.getIdAsignacion() != null) {
            asignacionRepository.findById(dto.getIdAsignacion())
                    .ifPresent(notificacion::setAsignacion);
        }

        return NotificacionResponseDTO.fromEntity(notificacionRepository.save(notificacion));
    }

    // Eliminar (borrado logico)
    public void eliminar(Long id) {
        Notificacion notificacion = buscarPorId(id);
        notificacion.setActivo(false);
        notificacionRepository.save(notificacion);
    }
}
