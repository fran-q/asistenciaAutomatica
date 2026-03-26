package com.appasistencia.services;

import com.appasistencia.models.*;
import com.appasistencia.repositories.AsistenciaRepository;
import com.appasistencia.repositories.HorarioRepository;
import com.appasistencia.repositories.UsuarioProfesorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

// Servicio: registro de asistencia por reconocimiento facial
@Service
public class FacialAttendanceService {

    private static final int TARDANZA_MINUTES = 15;

    private final HorarioRepository horarioRepository;
    private final AsistenciaRepository asistenciaRepository;
    private final UsuarioProfesorRepository profesorRepository;

    public FacialAttendanceService(HorarioRepository horarioRepository,
                                    AsistenciaRepository asistenciaRepository,
                                    UsuarioProfesorRepository profesorRepository) {
        this.horarioRepository = horarioRepository;
        this.asistenciaRepository = asistenciaRepository;
        this.profesorRepository = profesorRepository;
    }

    // Registrar asistencia facial: busca horario actual y determina PRESENTE/TARDANZA
    @Transactional
    public AttendanceResult registrarAsistenciaFacial(Long idUsuario) {
        // Find the UsuarioProfesor linked to this idUsuario
        UsuarioProfesor profesor = profesorRepository.findByUsuarioIdUsuario(idUsuario)
                .orElse(null);
        if (profesor == null) {
            return new AttendanceResult("ERROR", "Usuario no es profesor", null, null);
        }

        LocalDate hoy = LocalDate.now();
        LocalTime ahora = LocalTime.now();
        DiaSemana diaActual = toDiaSemana(hoy.getDayOfWeek());

        if (diaActual == null) {
            return new AttendanceResult("SIN_HORARIO", "Hoy es domingo", null, null);
        }

        // Find active schedules for this professor at this time
        List<Horario> horarios = horarioRepository.findActiveByDiaAndHoraAndProfesor(
                diaActual, ahora, profesor.getIdProfesor());

        if (horarios.isEmpty()) {
            return new AttendanceResult("SIN_HORARIO", "Sin clase programada en este momento", null, null);
        }

        // Take the first matching schedule
        Horario horario = horarios.get(0);
        Asignacion asignacion = horario.getAsignacion();

        // Check if attendance already exists for today
        boolean yaExiste = asistenciaRepository
                .existsByProfesorIdProfesorAndAsignacionIdAsignacionAndFechaAndActivoTrue(
                        profesor.getIdProfesor(), asignacion.getIdAsignacion(), hoy);

        if (yaExiste) {
            return new AttendanceResult("YA_REGISTRADA", "Asistencia ya registrada para esta clase",
                    asignacion.getIdAsignacion(), null);
        }

        // Determine status: PRESENTE or TARDANZA
        EstadoAsistencia estado = ahora.isAfter(horario.getHoraInicio().plusMinutes(TARDANZA_MINUTES))
                ? EstadoAsistencia.TARDANZA
                : EstadoAsistencia.PRESENTE;

        // Create attendance record
        Asistencia asistencia = new Asistencia(profesor, asignacion, hoy, ahora, estado, ModoRegistro.FACIAL);
        asistencia.setObservaciones("Registrado por reconocimiento facial");
        asistenciaRepository.save(asistencia);

        return new AttendanceResult("REGISTRADA", estado.name(),
                asignacion.getIdAsignacion(), asistencia.getIdAsistencia());
    }

    // Convertir DayOfWeek de Java a DiaSemana del sistema
    private DiaSemana toDiaSemana(DayOfWeek dow) {
        return switch (dow) {
            case MONDAY -> DiaSemana.LUNES;
            case TUESDAY -> DiaSemana.MARTES;
            case WEDNESDAY -> DiaSemana.MIERCOLES;
            case THURSDAY -> DiaSemana.JUEVES;
            case FRIDAY -> DiaSemana.VIERNES;
            case SATURDAY -> DiaSemana.SABADO;
            case SUNDAY -> null;
        };
    }

    public static class AttendanceResult {
        private final String status;
        private final String message;
        private final Long idAsignacion;
        private final Long idAsistencia;

        public AttendanceResult(String status, String message, Long idAsignacion, Long idAsistencia) {
            this.status = status;
            this.message = message;
            this.idAsignacion = idAsignacion;
            this.idAsistencia = idAsistencia;
        }

        public String getStatus() { return status; }
        public String getMessage() { return message; }
        public Long getIdAsignacion() { return idAsignacion; }
        public Long getIdAsistencia() { return idAsistencia; }
    }
}
