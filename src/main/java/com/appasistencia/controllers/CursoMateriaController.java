package com.appasistencia.controllers;

import com.appasistencia.dtos.CursoMateriaDTO;
import com.appasistencia.dtos.response.CursoMateriaResponseDTO;
import com.appasistencia.services.CursoMateriaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controlador REST: endpoints de relacion curso-materia
@RestController
@RequestMapping("/api/curso-materias")
public class CursoMateriaController {

    private final CursoMateriaService cursoMateriaService;

    public CursoMateriaController(CursoMateriaService cursoMateriaService) {
        this.cursoMateriaService = cursoMateriaService;
    }

    // GET /api/curso-materias - listar relaciones curso-materia por institucion
    @GetMapping
    public ResponseEntity<List<CursoMateriaResponseDTO>> listarTodos(HttpServletRequest request) {
        Long idInst = (Long) request.getAttribute("idInstitucion");
        return ResponseEntity.ok(cursoMateriaService.listarTodos(idInst));
    }

    // GET /api/curso-materias/{id} - obtener relacion por ID validando institucion
    @GetMapping("/{id}")
    public ResponseEntity<CursoMateriaResponseDTO> obtenerPorId(@PathVariable Long id, HttpServletRequest request) {
        // Obtener ID de institucion del token JWT
        Long idInst = (Long) request.getAttribute("idInstitucion");
        return ResponseEntity.ok(cursoMateriaService.obtenerPorId(id, idInst));
    }

    // GET /api/curso-materias/curso/{idCurso} - listar materias de un curso
    @GetMapping("/curso/{idCurso}")
    public ResponseEntity<List<CursoMateriaResponseDTO>> listarPorCurso(@PathVariable Long idCurso) {
        return ResponseEntity.ok(cursoMateriaService.listarPorCurso(idCurso));
    }

    // POST /api/curso-materias - vincular materia a curso
    @PostMapping
    public ResponseEntity<CursoMateriaResponseDTO> crear(@Valid @RequestBody CursoMateriaDTO dto) {
        CursoMateriaResponseDTO creado = cursoMateriaService.crear(dto);
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }

    // DELETE /api/curso-materias/{id} - baja logica validando institucion
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, HttpServletRequest request) {
        // Obtener ID de institucion del token JWT
        Long idInst = (Long) request.getAttribute("idInstitucion");
        // Validar que la entidad pertenece a la misma institucion
        cursoMateriaService.eliminar(id, idInst);
        return ResponseEntity.noContent().build();
    }

    // PATCH /api/curso-materias/{id}/reactivar - reactivar vinculo validando institucion
    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<Void> reactivar(@PathVariable Long id, HttpServletRequest request) {
        // Obtener ID de institucion del token JWT
        Long idInst = (Long) request.getAttribute("idInstitucion");
        // Validar que la entidad pertenece a la misma institucion
        cursoMateriaService.reactivar(id, idInst);
        return ResponseEntity.noContent().build();
    }
}
