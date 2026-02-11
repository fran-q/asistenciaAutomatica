package com.appasistencia.services;

import com.appasistencia.dtos.MateriaDTO;
import com.appasistencia.dtos.response.MateriaResponseDTO;
import com.appasistencia.exceptions.RecursoNoEncontradoException;
import com.appasistencia.models.Carrera;
import com.appasistencia.models.Materia;
import com.appasistencia.repositories.CarreraRepository;
import com.appasistencia.repositories.MateriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MateriaService {

    private final MateriaRepository materiaRepository;
    private final CarreraRepository carreraRepository;

    public MateriaService(MateriaRepository materiaRepository, CarreraRepository carreraRepository) {
        this.materiaRepository = materiaRepository;
        this.carreraRepository = carreraRepository;
    }

    @Transactional(readOnly = true)
    public List<MateriaResponseDTO> listarTodas() {
        return materiaRepository.findByActivoTrue().stream()
                .map(MateriaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MateriaResponseDTO obtenerPorId(Long id) {
        return MateriaResponseDTO.fromEntity(buscarPorId(id));
    }

    @Transactional(readOnly = true)
    public Materia buscarPorId(Long id) {
        return materiaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Materia", id));
    }

    @Transactional(readOnly = true)
    public List<MateriaResponseDTO> listarPorCarrera(Long idCarrera) {
        return materiaRepository.findByCarreraIdCarreraAndActivoTrue(idCarrera).stream()
                .map(MateriaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public MateriaResponseDTO crear(MateriaDTO dto) {
        Carrera carrera = carreraRepository.findById(dto.getIdCarrera())
                .orElseThrow(() -> new RecursoNoEncontradoException("Carrera", dto.getIdCarrera()));

        Materia materia = new Materia(dto.getNombre(), dto.getDescripcion(), dto.getHorasSemanales(), carrera);
        return MateriaResponseDTO.fromEntity(materiaRepository.save(materia));
    }

    public MateriaResponseDTO actualizar(Long id, MateriaDTO dto) {
        Materia materia = buscarPorId(id);
        materia.setNombre(dto.getNombre());
        materia.setDescripcion(dto.getDescripcion());
        materia.setHorasSemanales(dto.getHorasSemanales());

        if (dto.getIdCarrera() != null) {
            Carrera carrera = carreraRepository.findById(dto.getIdCarrera())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Carrera", dto.getIdCarrera()));
            materia.setCarrera(carrera);
        }

        return MateriaResponseDTO.fromEntity(materiaRepository.save(materia));
    }

    public void eliminar(Long id) {
        Materia materia = buscarPorId(id);
        materia.setActivo(false);
        materiaRepository.save(materia);
    }
}
