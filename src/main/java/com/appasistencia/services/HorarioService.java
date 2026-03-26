package com.appasistencia.services;

import com.appasistencia.dtos.HorarioDTO;
import com.appasistencia.dtos.response.HorarioResponseDTO;
import com.appasistencia.exceptions.RecursoNoEncontradoException;
import com.appasistencia.exceptions.OperacionInvalidaException;
import com.appasistencia.models.Asignacion;
import com.appasistencia.models.DiaSemana;
import com.appasistencia.models.Horario;
import com.appasistencia.repositories.AsignacionRepository;
import com.appasistencia.repositories.HorarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

// Servicio: logica de negocio para horarios de clase
@Service
@Transactional
public class HorarioService {

    private final HorarioRepository horarioRepository;
    private final AsignacionRepository asignacionRepository;

    public HorarioService(HorarioRepository horarioRepository, AsignacionRepository asignacionRepository) {
        this.horarioRepository = horarioRepository;
        this.asignacionRepository = asignacionRepository;
    }

    @Transactional(readOnly = true)
    public List<HorarioResponseDTO> listarTodos() {
        return horarioRepository.findAll().stream()
                .map(HorarioResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Listar filtrado por institucion
    @Transactional(readOnly = true)
    public List<HorarioResponseDTO> listarTodos(Long idInstitucion) {
        return horarioRepository.findByInstitucion(idInstitucion).stream()
                .map(HorarioResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public HorarioResponseDTO obtenerPorId(Long id) {
        return HorarioResponseDTO.fromEntity(buscarPorId(id));
    }

    @Transactional(readOnly = true)
    public Horario buscarPorId(Long id) {
        return horarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Horario", id));
    }

    @Transactional(readOnly = true)
    public List<HorarioResponseDTO> listarPorAsignacion(Long idAsignacion) {
        return horarioRepository.findByAsignacionIdAsignacionAndActivoTrue(idAsignacion).stream()
                .map(HorarioResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HorarioResponseDTO> listarPorDia(String dia) {
        try {
            DiaSemana diaSemana = DiaSemana.valueOf(dia.toUpperCase());
            return horarioRepository.findByDiaSemanaAndActivoTrue(diaSemana).stream()
                    .map(HorarioResponseDTO::fromEntity)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new OperacionInvalidaException("Dia invalido: " + dia + ". Valores permitidos: LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO");
        }
    }

    // Crear validando dia, horas y que horaFin > horaInicio
    public HorarioResponseDTO crear(HorarioDTO dto) {
        Asignacion asignacion = asignacionRepository.findById(dto.getIdAsignacion())
                .orElseThrow(() -> new RecursoNoEncontradoException("Asignacion", dto.getIdAsignacion()));

        DiaSemana diaSemana;
        try {
            diaSemana = DiaSemana.valueOf(dto.getDiaSemana().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new OperacionInvalidaException("Dia invalido: " + dto.getDiaSemana());
        }

        LocalTime horaInicio = parseTime(dto.getHoraInicio(), "hora de inicio");
        LocalTime horaFin = parseTime(dto.getHoraFin(), "hora de fin");

        if (horaFin.isBefore(horaInicio) || horaFin.equals(horaInicio)) {
            throw new OperacionInvalidaException("La hora de fin debe ser posterior a la hora de inicio");
        }

        Horario horario = new Horario(asignacion, diaSemana, horaInicio, horaFin);
        return HorarioResponseDTO.fromEntity(horarioRepository.save(horario));
    }

    public HorarioResponseDTO actualizar(Long id, HorarioDTO dto) {
        Horario horario = buscarPorId(id);

        if (dto.getDiaSemana() != null) {
            try {
                horario.setDiaSemana(DiaSemana.valueOf(dto.getDiaSemana().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new OperacionInvalidaException("Dia invalido: " + dto.getDiaSemana());
            }
        }
        if (dto.getHoraInicio() != null) horario.setHoraInicio(parseTime(dto.getHoraInicio(), "hora de inicio"));
        if (dto.getHoraFin() != null) horario.setHoraFin(parseTime(dto.getHoraFin(), "hora de fin"));

        if (dto.getIdAsignacion() != null) {
            Asignacion asignacion = asignacionRepository.findById(dto.getIdAsignacion())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Asignacion", dto.getIdAsignacion()));
            horario.setAsignacion(asignacion);
        }

        return HorarioResponseDTO.fromEntity(horarioRepository.save(horario));
    }

    // Eliminar (borrado logico)
    public void eliminar(Long id) {
        Horario horario = buscarPorId(id);
        horario.setActivo(false);
        horarioRepository.save(horario);
    }

    public void reactivar(Long id) {
        Horario horario = buscarPorId(id);
        horario.setActivo(true);
        horarioRepository.save(horario);
    }

    private LocalTime parseTime(String time, String campo) {
        try {
            return LocalTime.parse(time);
        } catch (DateTimeParseException e) {
            throw new OperacionInvalidaException("Formato invalido para " + campo + ": " + time + ". Use formato HH:mm");
        }
    }

    // === Metodos con validacion de institucion ===
    // Estos metodos verifican que el recurso pertenezca a la institucion del usuario autenticado

    // Verifica que el recurso pertenezca a la institucion del usuario autenticado
    private void verificarInstitucion(Long idInstitucionRecurso, Long idInstitucionUsuario) {
        if (!idInstitucionRecurso.equals(idInstitucionUsuario)) {
            throw new RecursoNoEncontradoException("Horario", 0L);
        }
    }

    // Obtener la institucion de un horario navegando la cadena de relaciones
    private Long obtenerIdInstitucionDeHorario(Horario horario) {
        return horario.getAsignacion().getCursoMateria().getCurso().getCarrera().getInstitucion().getIdInstitucion();
    }

    // Obtener horario por ID validando que pertenece a la misma institucion
    @Transactional(readOnly = true)
    public HorarioResponseDTO obtenerPorId(Long id, Long idInstitucion) {
        Horario horario = buscarPorId(id);
        verificarInstitucion(obtenerIdInstitucionDeHorario(horario), idInstitucion);
        return HorarioResponseDTO.fromEntity(horario);
    }

    // Actualizar horario validando que pertenece a la misma institucion
    public HorarioResponseDTO actualizar(Long id, HorarioDTO dto, Long idInstitucion) {
        Horario horario = buscarPorId(id);
        verificarInstitucion(obtenerIdInstitucionDeHorario(horario), idInstitucion);
        return actualizar(id, dto);
    }

    // Eliminar horario validando que pertenece a la misma institucion
    public void eliminar(Long id, Long idInstitucion) {
        Horario horario = buscarPorId(id);
        verificarInstitucion(obtenerIdInstitucionDeHorario(horario), idInstitucion);
        eliminar(id);
    }

    // Reactivar horario validando que pertenece a la misma institucion
    public void reactivar(Long id, Long idInstitucion) {
        Horario horario = buscarPorId(id);
        verificarInstitucion(obtenerIdInstitucionDeHorario(horario), idInstitucion);
        reactivar(id);
    }
}
