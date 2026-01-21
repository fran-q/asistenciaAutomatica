package com.appasistencia.controllers;

import com.appasistencia.dtos.HorarioDTO;
import com.appasistencia.models.Horario;
import com.appasistencia.models.Usuario;
import com.appasistencia.repositories.HorarioRepository;
import com.appasistencia.repositories.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class HorarioController {

    private final HorarioRepository horarioRepository;
    private final UsuarioRepository usuarioRepository;

    public HorarioController(HorarioRepository horarioRepository, UsuarioRepository usuarioRepository) {
        this.horarioRepository = horarioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    //Todos los horarios
    @GetMapping("/horarios")
    public List<HorarioDTO> getHorarios() {
        return horarioRepository.findAll()
                .stream()
                .map(HorarioDTO::new)
                .toList();
    }

    //Horario por ID
    @GetMapping("/horarios/{id}")
    public ResponseEntity<HorarioDTO> getHorario(@PathVariable Integer id) {
        return horarioRepository.findById(id)
                .map(horario -> ResponseEntity.ok(new HorarioDTO(horario)))
                .orElse(ResponseEntity.notFound().build());
    }

    //Agregar horario
    @PostMapping("/usuarios/{usuarioId}/horarios")
    public ResponseEntity<?> crearHorario(@PathVariable Integer usuarioId, @RequestBody Horario horario) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }
        usuario.addHorario(horario);
        usuarioRepository.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Horario creado correctamente");
    }

    //Actualizacion de horario, no cambia el
    @PutMapping("/horarios/{id}")
    public ResponseEntity<?> actualizarHorario(@PathVariable Integer id, @RequestBody Horario nuevosDatos) {
        return horarioRepository.findById(id)
                .map(horario -> {
                    horario.setDiaSemana(nuevosDatos.getDiaSemana());
                    horario.setHoraEntrada(nuevosDatos.getHoraEntrada());
                    horario.setHoraSalida(nuevosDatos.getHoraSalida());
                    horarioRepository.save(horario);
                    return ResponseEntity.ok("Horario actualizado correctamente");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    //Baja logica del horario
    @PatchMapping("/horarios/{id}/baja")
    public ResponseEntity<?> darBajaHorario(@PathVariable Integer id) {
        return horarioRepository.findById(id)
                .map(horario -> {
                    horario.setEsActivo(false);
                    horarioRepository.save(horario);
                    return ResponseEntity.ok("Horario dado de baja correctamente");
                })
                .orElse(ResponseEntity.notFound().build());
    }
}