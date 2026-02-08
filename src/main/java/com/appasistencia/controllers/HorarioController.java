package com.appasistencia.controllers;

import com.appasistencia.dtos.HorarioDTO;
import com.appasistencia.models.DiaSemana;
import com.appasistencia.models.Horario;
import com.appasistencia.repositories.AsignacionRepository;
import com.appasistencia.repositories.HorarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/horarios")
public class HorarioController {

    private final HorarioRepository horarioRepository;
    private final AsignacionRepository asignacionRepository;

    public HorarioController(HorarioRepository horarioRepository, AsignacionRepository asignacionRepository) {
        this.horarioRepository = horarioRepository;
        this.asignacionRepository = asignacionRepository;
    }

    @GetMapping
    public List<Horario> listarTodos() {
        return horarioRepository.findByActivoTrue();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Horario> obtenerPorId(@PathVariable Long id) {
        return horarioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/asignacion/{idAsignacion}")
    public List<Horario> listarPorAsignacion(@PathVariable Long idAsignacion) {
        return horarioRepository.findByAsignacionIdAsignacionAndActivoTrue(idAsignacion);
    }

    @GetMapping("/dia/{dia}")
    public List<Horario> listarPorDia(@PathVariable String dia) {
        return horarioRepository.findByDiaSemanaAndActivoTrue(DiaSemana.valueOf(dia.toUpperCase()));
    }

    @PostMapping
    public ResponseEntity<Horario> crear(@RequestBody HorarioDTO dto) {
        return asignacionRepository.findById(dto.getIdAsignacion()).map(asignacion -> {
            Horario horario = new Horario(
                    asignacion,
                    DiaSemana.valueOf(dto.getDiaSemana()),
                    LocalTime.parse(dto.getHoraInicio()),
                    LocalTime.parse(dto.getHoraFin())
            );
            return ResponseEntity.ok(horarioRepository.save(horario));
        }).orElse(ResponseEntity.badRequest().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Horario> actualizar(@PathVariable Long id, @RequestBody HorarioDTO dto) {
        return horarioRepository.findById(id).map(horario -> {
            if (dto.getDiaSemana() != null) horario.setDiaSemana(DiaSemana.valueOf(dto.getDiaSemana()));
            if (dto.getHoraInicio() != null) horario.setHoraInicio(LocalTime.parse(dto.getHoraInicio()));
            if (dto.getHoraFin() != null) horario.setHoraFin(LocalTime.parse(dto.getHoraFin()));
            if (dto.getIdAsignacion() != null) {
                asignacionRepository.findById(dto.getIdAsignacion()).ifPresent(horario::setAsignacion);
            }
            return ResponseEntity.ok(horarioRepository.save(horario));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return horarioRepository.findById(id).map(horario -> {
            horario.setActivo(false);
            horarioRepository.save(horario);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
