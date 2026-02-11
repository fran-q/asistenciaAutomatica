package com.appasistencia.services;

import com.appasistencia.dtos.AsistenciaDTO;
import com.appasistencia.dtos.response.AsistenciaResponseDTO;
import com.appasistencia.exceptions.RecursoNoEncontradoException;
import com.appasistencia.exceptions.OperacionInvalidaException;
import com.appasistencia.models.*;
import com.appasistencia.repositories.AsignacionRepository;
import com.appasistencia.repositories.AsistenciaRepository;
import com.appasistencia.repositories.UsuarioProfesorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AsistenciaService {

    private final AsistenciaRepository asistenciaRepository;
    private final UsuarioProfesorRepository profesorRepository;
    private final AsignacionRepository asignacionRepository;

    public AsistenciaService(AsistenciaRepository asistenciaRepository,
                              UsuarioProfesorRepository profesorRepository,
                              AsignacionRepository asignacionRepository) {
        this.asistenciaRepository = asistenciaRepository;
        this.profesorRepository = profesorRepository;
        this.asignacionRepository = asignacionRepository;
    }

    @Transactional(readOnly = true)
    public List<AsistenciaResponseDTO> listarTodas() {
        return asistenciaRepository.findByActivoTrue().stream()
                .map(AsistenciaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AsistenciaResponseDTO obtenerPorId(Long id) {
        return AsistenciaResponseDTO.fromEntity(buscarPorId(id));
    }

    @Transactional(readOnly = true)
    public Asistencia buscarPorId(Long id) {
        return asistenciaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Asistencia", id));
    }

    @Transactional(readOnly = true)
    public List<AsistenciaResponseDTO> listarPorProfesor(Long idProfesor) {
        return asistenciaRepository.findByProfesorIdProfesorAndActivoTrue(idProfesor).stream()
                .map(AsistenciaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AsistenciaResponseDTO> listarPorAsignacion(Long idAsignacion) {
        return asistenciaRepository.findByAsignacionIdAsignacionAndActivoTrue(idAsignacion).stream()
                .map(AsistenciaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AsistenciaResponseDTO> listarPorFecha(String fecha) {
        LocalDate date = parseDate(fecha);
        return asistenciaRepository.findByFechaAndActivoTrue(date).stream()
                .map(AsistenciaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AsistenciaResponseDTO> listarPorRango(String desde, String hasta) {
        LocalDate fechaDesde = parseDate(desde);
        LocalDate fechaHasta = parseDate(hasta);
        if (fechaHasta.isBefore(fechaDesde)) {
            throw new OperacionInvalidaException("La fecha 'hasta' no puede ser anterior a la fecha 'desde'");
        }
        return asistenciaRepository.findByFechaBetweenAndActivoTrue(fechaDesde, fechaHasta).stream()
                .map(AsistenciaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public AsistenciaResponseDTO crear(AsistenciaDTO dto) {
        UsuarioProfesor profesor = profesorRepository.findById(dto.getIdProfesor())
                .orElseThrow(() -> new RecursoNoEncontradoException("Profesor", dto.getIdProfesor()));
        Asignacion asignacion = asignacionRepository.findById(dto.getIdAsignacion())
                .orElseThrow(() -> new RecursoNoEncontradoException("Asignacion", dto.getIdAsignacion()));

        EstadoAsistencia estado;
        ModoRegistro modo;
        try {
            estado = EstadoAsistencia.valueOf(dto.getEstado().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new OperacionInvalidaException("Estado invalido: " + dto.getEstado() + ". Valores permitidos: PRESENTE, AUSENTE, TARDANZA, JUSTIFICADO");
        }
        try {
            modo = ModoRegistro.valueOf(dto.getModoRegistro().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new OperacionInvalidaException("Modo de registro invalido: " + dto.getModoRegistro() + ". Valores permitidos: FACIAL, MANUAL");
        }

        Asistencia asistencia = new Asistencia(
                profesor, asignacion,
                parseDate(dto.getFecha()),
                parseTime(dto.getHoraEntrada(), "hora de entrada"),
                estado, modo
        );
        if (dto.getHoraSalida() != null && !dto.getHoraSalida().isBlank()) {
            asistencia.setHoraSalida(parseTime(dto.getHoraSalida(), "hora de salida"));
        }
        asistencia.setObservaciones(dto.getObservaciones());

        return AsistenciaResponseDTO.fromEntity(asistenciaRepository.save(asistencia));
    }

    public AsistenciaResponseDTO actualizar(Long id, AsistenciaDTO dto) {
        Asistencia asistencia = buscarPorId(id);

        if (dto.getFecha() != null) asistencia.setFecha(parseDate(dto.getFecha()));
        if (dto.getHoraEntrada() != null) asistencia.setHoraEntrada(parseTime(dto.getHoraEntrada(), "hora de entrada"));
        if (dto.getHoraSalida() != null) asistencia.setHoraSalida(parseTime(dto.getHoraSalida(), "hora de salida"));
        if (dto.getEstado() != null) {
            try {
                asistencia.setEstado(EstadoAsistencia.valueOf(dto.getEstado().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new OperacionInvalidaException("Estado invalido: " + dto.getEstado());
            }
        }
        if (dto.getModoRegistro() != null) {
            try {
                asistencia.setModoRegistro(ModoRegistro.valueOf(dto.getModoRegistro().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new OperacionInvalidaException("Modo de registro invalido: " + dto.getModoRegistro());
            }
        }
        asistencia.setObservaciones(dto.getObservaciones());

        return AsistenciaResponseDTO.fromEntity(asistenciaRepository.save(asistencia));
    }

    public void eliminar(Long id) {
        Asistencia asistencia = buscarPorId(id);
        asistencia.setActivo(false);
        asistenciaRepository.save(asistencia);
    }

    private LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new OperacionInvalidaException("Formato de fecha invalido: " + date + ". Use formato yyyy-MM-dd");
        }
    }

    private LocalTime parseTime(String time, String campo) {
        try {
            return LocalTime.parse(time);
        } catch (DateTimeParseException e) {
            throw new OperacionInvalidaException("Formato invalido para " + campo + ": " + time + ". Use formato HH:mm");
        }
    }
}
