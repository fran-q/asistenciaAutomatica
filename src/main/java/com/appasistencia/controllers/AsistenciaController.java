package com.appasistencia.controllers;

import com.appasistencia.dtos.AsistenciaDTO;
import com.appasistencia.models.Asistencia;
import com.appasistencia.models.EstadoAsistencia;
import com.appasistencia.models.ModoRegistro;
import com.appasistencia.repositories.AsignacionRepository;
import com.appasistencia.repositories.AsistenciaRepository;
import com.appasistencia.repositories.UsuarioProfesorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/asistencias")
public class AsistenciaController {

    private final AsistenciaRepository asistenciaRepository;
    private final UsuarioProfesorRepository profesorRepository;
    private final AsignacionRepository asignacionRepository;

    public AsistenciaController(AsistenciaRepository asistenciaRepository,
                                 UsuarioProfesorRepository profesorRepository,
                                 AsignacionRepository asignacionRepository) {
        this.asistenciaRepository = asistenciaRepository;
        this.profesorRepository = profesorRepository;
        this.asignacionRepository = asignacionRepository;
    }

    @GetMapping
    public List<Asistencia> listarTodas() {
        return asistenciaRepository.findByActivoTrue();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Asistencia> obtenerPorId(@PathVariable Long id) {
        return asistenciaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/profesor/{idProfesor}")
    public List<Asistencia> listarPorProfesor(@PathVariable Long idProfesor) {
        return asistenciaRepository.findByProfesorIdProfesorAndActivoTrue(idProfesor);
    }

    @GetMapping("/asignacion/{idAsignacion}")
    public List<Asistencia> listarPorAsignacion(@PathVariable Long idAsignacion) {
        return asistenciaRepository.findByAsignacionIdAsignacionAndActivoTrue(idAsignacion);
    }

    @GetMapping("/fecha/{fecha}")
    public List<Asistencia> listarPorFecha(@PathVariable String fecha) {
        return asistenciaRepository.findByFechaAndActivoTrue(LocalDate.parse(fecha));
    }

    @GetMapping("/rango")
    public List<Asistencia> listarPorRango(@RequestParam String desde, @RequestParam String hasta) {
        return asistenciaRepository.findByFechaBetweenAndActivoTrue(
                LocalDate.parse(desde), LocalDate.parse(hasta)
        );
    }

    @PostMapping
    public ResponseEntity<Asistencia> crear(@RequestBody AsistenciaDTO dto) {
        var profesorOpt = profesorRepository.findById(dto.getIdProfesor());
        var asignacionOpt = asignacionRepository.findById(dto.getIdAsignacion());

        if (profesorOpt.isPresent() && asignacionOpt.isPresent()) {
            Asistencia asistencia = new Asistencia(
                    profesorOpt.get(), asignacionOpt.get(),
                    LocalDate.parse(dto.getFecha()),
                    LocalTime.parse(dto.getHoraEntrada()),
                    EstadoAsistencia.valueOf(dto.getEstado()),
                    ModoRegistro.valueOf(dto.getModoRegistro())
            );
            if (dto.getHoraSalida() != null) asistencia.setHoraSalida(LocalTime.parse(dto.getHoraSalida()));
            asistencia.setObservaciones(dto.getObservaciones());

            return ResponseEntity.ok(asistenciaRepository.save(asistencia));
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Asistencia> actualizar(@PathVariable Long id, @RequestBody AsistenciaDTO dto) {
        return asistenciaRepository.findById(id).map(asistencia -> {
            if (dto.getFecha() != null) asistencia.setFecha(LocalDate.parse(dto.getFecha()));
            if (dto.getHoraEntrada() != null) asistencia.setHoraEntrada(LocalTime.parse(dto.getHoraEntrada()));
            if (dto.getHoraSalida() != null) asistencia.setHoraSalida(LocalTime.parse(dto.getHoraSalida()));
            if (dto.getEstado() != null) asistencia.setEstado(EstadoAsistencia.valueOf(dto.getEstado()));
            if (dto.getModoRegistro() != null) asistencia.setModoRegistro(ModoRegistro.valueOf(dto.getModoRegistro()));
            asistencia.setObservaciones(dto.getObservaciones());
            return ResponseEntity.ok(asistenciaRepository.save(asistencia));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return asistenciaRepository.findById(id).map(asistencia -> {
            asistencia.setActivo(false);
            asistenciaRepository.save(asistencia);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
