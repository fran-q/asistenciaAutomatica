package com.appasistencia.controllers;

import com.appasistencia.dtos.NotificacionDTO;
import com.appasistencia.dtos.response.NotificacionResponseDTO;
import com.appasistencia.services.NotificacionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @GetMapping
    public ResponseEntity<List<NotificacionResponseDTO>> listarTodas() {
        return ResponseEntity.ok(notificacionService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificacionResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(notificacionService.obtenerPorId(id));
    }

    @GetMapping("/alumno/{idAlumno}")
    public ResponseEntity<List<NotificacionResponseDTO>> listarPorAlumno(@PathVariable Long idAlumno) {
        return ResponseEntity.ok(notificacionService.listarPorAlumno(idAlumno));
    }

    @PostMapping
    public ResponseEntity<NotificacionResponseDTO> crear(@Valid @RequestBody NotificacionDTO dto) {
        NotificacionResponseDTO creada = notificacionService.crear(dto);
        return new ResponseEntity<>(creada, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificacionResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody NotificacionDTO dto) {
        return ResponseEntity.ok(notificacionService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        notificacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
