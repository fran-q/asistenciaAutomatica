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

@Service
@Transactional
public class CarreraService {

    private final CarreraRepository carreraRepository;
    private final InstitucionRepository institucionRepository;

    public CarreraService(CarreraRepository carreraRepository, InstitucionRepository institucionRepository) {
        this.carreraRepository = carreraRepository;
        this.institucionRepository = institucionRepository;
    }

    @Transactional(readOnly = true)
    public List<CarreraResponseDTO> listarTodas() {
        return carreraRepository.findByActivoTrue().stream()
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

    @Transactional(readOnly = true)
    public List<CarreraResponseDTO> listarPorInstitucion(Long idInstitucion) {
        return carreraRepository.findByInstitucionIdInstitucionAndActivoTrue(idInstitucion).stream()
                .map(CarreraResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

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

    public void eliminar(Long id) {
        Carrera carrera = buscarPorId(id);
        carrera.setActivo(false);
        carreraRepository.save(carrera);
    }
}
