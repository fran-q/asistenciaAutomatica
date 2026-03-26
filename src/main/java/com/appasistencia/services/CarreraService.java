package com.appasistencia.services;

import com.appasistencia.dtos.CarreraDTO;
import com.appasistencia.dtos.response.CarreraResponseDTO;
import com.appasistencia.exceptions.RecursoNoEncontradoException;
import com.appasistencia.models.Carrera;
import com.appasistencia.models.Institucion;
import com.appasistencia.repositories.CarreraRepository;
import com.appasistencia.repositories.InstitucionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// Servicio: logica de negocio para carreras
@Service
@Transactional
public class CarreraService {

    private final CarreraRepository carreraRepository;
    private final InstitucionRepository institucionRepository;

    public CarreraService(CarreraRepository carreraRepository, InstitucionRepository institucionRepository) {
        this.carreraRepository = carreraRepository;
        this.institucionRepository = institucionRepository;
    }

    // Listado (todas o filtradas por institucion)
    @Transactional(readOnly = true)
    public List<CarreraResponseDTO> listarTodas() {
        return carreraRepository.findAll().stream()
                .map(CarreraResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CarreraResponseDTO> listarTodas(Long idInstitucion) {
        return carreraRepository.findByInstitucionIdInstitucion(idInstitucion).stream()
                .map(CarreraResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CarreraResponseDTO obtenerPorId(Long id) {
        return CarreraResponseDTO.fromEntity(buscarPorId(id));
    }

    @Transactional(readOnly = true)
    public Carrera buscarPorId(Long id) {
        return carreraRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Carrera", id));
    }

    // Filtrado por institucion (solo activas)
    @Transactional(readOnly = true)
    public List<CarreraResponseDTO> listarPorInstitucion(Long idInstitucion) {
        return carreraRepository.findByInstitucionIdInstitucionAndActivoTrue(idInstitucion).stream()
                .map(CarreraResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // CRUD - valida existencia de institucion
    public CarreraResponseDTO crear(CarreraDTO dto) {
        Institucion institucion = institucionRepository.findById(dto.getIdInstitucion())
                .orElseThrow(() -> new RecursoNoEncontradoException("Institucion", dto.getIdInstitucion()));

        Carrera carrera = new Carrera(
                dto.getNombre(), dto.getDescripcion(), dto.getDuracionAnios(),
                dto.getTitulo(), institucion
        );
        return CarreraResponseDTO.fromEntity(carreraRepository.save(carrera));
    }

    public CarreraResponseDTO actualizar(Long id, CarreraDTO dto) {
        Carrera carrera = buscarPorId(id);
        carrera.setNombre(dto.getNombre());
        carrera.setDescripcion(dto.getDescripcion());
        carrera.setDuracionAnios(dto.getDuracionAnios());
        carrera.setTitulo(dto.getTitulo());

        if (dto.getIdInstitucion() != null) {
            Institucion institucion = institucionRepository.findById(dto.getIdInstitucion())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Institucion", dto.getIdInstitucion()));
            carrera.setInstitucion(institucion);
        }

        return CarreraResponseDTO.fromEntity(carreraRepository.save(carrera));
    }

    // Baja logica y reactivacion
    public void eliminar(Long id) {
        Carrera carrera = buscarPorId(id);
        carrera.setActivo(false);
        carreraRepository.save(carrera);
    }

    public void reactivar(Long id) {
        Carrera carrera = buscarPorId(id);
        carrera.setActivo(true);
        carreraRepository.save(carrera);
    }

    // === Metodos con validacion de institucion ===
    // Estos metodos verifican que el recurso pertenezca a la institucion del usuario autenticado

    // Verifica que el recurso pertenezca a la institucion del usuario autenticado
    private void verificarInstitucion(Long idInstitucionRecurso, Long idInstitucionUsuario) {
        if (!idInstitucionRecurso.equals(idInstitucionUsuario)) {
            throw new RecursoNoEncontradoException("Carrera", 0L);
        }
    }

    // Obtener carrera por ID validando que pertenece a la misma institucion
    @Transactional(readOnly = true)
    public CarreraResponseDTO obtenerPorId(Long id, Long idInstitucion) {
        Carrera carrera = buscarPorId(id);
        verificarInstitucion(carrera.getInstitucion().getIdInstitucion(), idInstitucion);
        return CarreraResponseDTO.fromEntity(carrera);
    }

    // Actualizar carrera validando que pertenece a la misma institucion
    public CarreraResponseDTO actualizar(Long id, CarreraDTO dto, Long idInstitucion) {
        Carrera carrera = buscarPorId(id);
        verificarInstitucion(carrera.getInstitucion().getIdInstitucion(), idInstitucion);
        return actualizar(id, dto);
    }

    // Eliminar carrera validando que pertenece a la misma institucion
    public void eliminar(Long id, Long idInstitucion) {
        Carrera carrera = buscarPorId(id);
        verificarInstitucion(carrera.getInstitucion().getIdInstitucion(), idInstitucion);
        eliminar(id);
    }

    // Reactivar carrera validando que pertenece a la misma institucion
    public void reactivar(Long id, Long idInstitucion) {
        Carrera carrera = buscarPorId(id);
        verificarInstitucion(carrera.getInstitucion().getIdInstitucion(), idInstitucion);
        reactivar(id);
    }
}
