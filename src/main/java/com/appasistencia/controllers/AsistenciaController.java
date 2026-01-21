package com.appasistencia.controllers;

import com.appasistencia.dtos.AsistenciaDTO;
import com.appasistencia.models.Asistencia;
import com.appasistencia.models.Usuario;
import com.appasistencia.repositories.AsistenciaRepository;
import com.appasistencia.repositories.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AsistenciaController {

    private final AsistenciaRepository asistenciaRepository;
    private final UsuarioRepository usuarioRepository;

    public AsistenciaController(AsistenciaRepository asistenciaRepository, UsuarioRepository usuarioRepository) {
        this.asistenciaRepository = asistenciaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    //Todas las asistencias
    @GetMapping("/asistencias")
    public List<AsistenciaDTO> getAsistencias() {
        return asistenciaRepository.findAll()
                .stream()
                .map(AsistenciaDTO::new)
                .toList();
    }

    //Asistencia por ID
    @GetMapping("/asistencias/{id}")
    public ResponseEntity<AsistenciaDTO> getAsistencia(@PathVariable Integer id) {
        return asistenciaRepository.findById(id)
                .map(asistencia -> ResponseEntity.ok(new AsistenciaDTO(asistencia)))
                .orElse(ResponseEntity.notFound().build());
    }

    //Obtiene todas las asistencias de un usuario
    @GetMapping("/usuarios/{usuarioId}/asistencias")
    public ResponseEntity<List<AsistenciaDTO>> getAsistenciasByUsuario(@PathVariable Integer usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<AsistenciaDTO> asistencias = asistenciaRepository
                .findByUsuario(usuario)         // 👈 necesitás este método en el repo
                .stream()
                .map(AsistenciaDTO::new)
                .collect(Collectors.toList());

        return new ResponseEntity<>(asistencias, HttpStatus.OK);
    }

    //Ingreso de una asistencia
    @PostMapping("/usuarios/{usuarioId}/asistencias")
    public ResponseEntity<?> crearAsistencia(@PathVariable Integer usuarioId, @RequestBody Asistencia asistencia) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }
        // Asociar la asistencia al usuario y persistir
        usuario.addAsistencia(asistencia);
        usuarioRepository.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Asistencia registrada correctamente");
    }

    //Actualizacion de una asistecia
    @PutMapping("/asistencias/{id}")
    public ResponseEntity<?> actualizarAsistencia(@PathVariable Integer id, @RequestBody Asistencia nuevosDatos) {
        return asistenciaRepository.findById(id)
                .map(asistencia -> {
                    asistencia.setHoraIngresoRegistro(nuevosDatos.getHoraIngresoRegistro());
                    asistencia.setHoraSalidaRegistro(nuevosDatos.getHoraSalidaRegistro());
                    asistencia.setVerificadoBiometrico(nuevosDatos.getVerificadoBiometrico());
                    asistencia.setObservaciones(nuevosDatos.getObservaciones());
                    asistenciaRepository.save(asistencia);
                    return ResponseEntity.ok("Asistencia actualizada correctamente");
                })
                .orElse(ResponseEntity.notFound().build());
    }
}