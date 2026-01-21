package com.appasistencia.controllers;

import com.appasistencia.dtos.UsuarioPlantillaBiometricaDTO;
import com.appasistencia.models.PlantillaBiometrica;
import com.appasistencia.models.Usuario;
import com.appasistencia.models.UsuarioPlantillaBiometrica;
import com.appasistencia.repositories.PlantillaBiometricaRepository;
import com.appasistencia.repositories.UsuarioPlantillaBiometricaRepository;
import com.appasistencia.repositories.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UsuarioPlantillaBiometricaController {

    private final UsuarioPlantillaBiometricaRepository upbRepository;
    private final UsuarioRepository usuarioRepository;
    private final PlantillaBiometricaRepository plantillaRepository;

    public UsuarioPlantillaBiometricaController(UsuarioPlantillaBiometricaRepository upbRepository,
                                                UsuarioRepository usuarioRepository,
                                                PlantillaBiometricaRepository plantillaRepository) {
        this.upbRepository = upbRepository;
        this.usuarioRepository = usuarioRepository;
        this.plantillaRepository = plantillaRepository;
    }

    //listado de todas las relaciones usuario-plantilla
    @GetMapping("/usuario_plantillas")
    public List<UsuarioPlantillaBiometricaDTO> getRelaciones() {
        return upbRepository.findAll()
                .stream()
                .map(UsuarioPlantillaBiometricaDTO::new)
                .toList();
    }

    //Busqueda de usuario-plantilla por ID
    @GetMapping("/usuario_plantillas/{id}")
    public ResponseEntity<UsuarioPlantillaBiometricaDTO> getRelacion(@PathVariable Integer id) {
        return upbRepository.findById(id)
                .map(upb -> ResponseEntity.ok(new UsuarioPlantillaBiometricaDTO(upb)))
                .orElse(ResponseEntity.notFound().build());
    }

    //Creamos una nueva relacion usuario-plantilla, llamando por ID al usuario y plantilla
    @PostMapping("/usuarios/{usuarioId}/plantillas/{plantillaId}")
    public ResponseEntity<?> crearRelacion(@PathVariable Integer usuarioId, @PathVariable Integer plantillaId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
        PlantillaBiometrica plantilla = plantillaRepository.findById(plantillaId).orElse(null);
        if (plantilla == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Plantilla no encontrada");
        }
        // Crear y asociar la relacion
        UsuarioPlantillaBiometrica upb = new UsuarioPlantillaBiometrica();
        upb.setUsuario(usuario);
        upb.setPlantillaBiometrica(plantilla);
        upb.setEsActivo(true);
        upbRepository.save(upb);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Relación creada correctamente");
    }

    //baja logica de usuario-plantilla
    @PatchMapping("/usuario_plantillas/{id}/baja")
    public ResponseEntity<?> darBajaRelacion(@PathVariable Integer id) {
        return upbRepository.findById(id)
                .map(upb -> {
                    upb.setEsActivo(false);
                    upbRepository.save(upb);
                    return ResponseEntity.ok("Relación dada de baja correctamente");
                })
                .orElse(ResponseEntity.notFound().build());
    }
}