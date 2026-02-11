package com.appasistencia.services;

import com.appasistencia.dtos.CursoDTO;
import com.appasistencia.dtos.response.CursoResponseDTO;
import com.appasistencia.exceptions.RecursoNoEncontradoException;
import com.appasistencia.exceptions.OperacionInvalidaException;
import com.appasistencia.models.Carrera;
import com.appasistencia.models.Curso;
import com.appasistencia.models.Turno;
import com.appasistencia.repositories.CarreraRepository;
import com.appasistencia.repositories.CursoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CursoService {

    private final CursoRepository cursoRepository;
    private final CarreraRepository carreraRepository;

    public CursoService(CursoRepository cursoRepository, CarreraRepository carreraRepository) {
        this.cursoRepository = cursoRepository;
        this.carreraRepository = carreraRepository;
    }

    @Transactional(readOnly = true)
    public List<CursoResponseDTO> listarTodos() {
        return cursoRepository.findByActivoTrue().stream()
                .map(CursoResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CursoResponseDTO obtenerPorId(Long id) {
        return CursoResponseDTO.fromEntity(buscarPorId(id));
    }

    @Transactional(readOnly = true)
    public Curso buscarPorId(Long id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Curso", id));
    }

    @Transactional(readOnly = true)
    public List<CursoResponseDTO> listarPorCarrera(Long idCarrera) {
        return cursoRepository.findByCarreraIdCarreraAndActivoTrue(idCarrera).stream()
                .map(CursoResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public CursoResponseDTO crear(CursoDTO dto) {
        Carrera carrera = carreraRepository.findById(dto.getIdCarrera())
                .orElseThrow(() -> new RecursoNoEncontradoException("Carrera", dto.getIdCarrera()));

        try {
            Curso curso = new Curso(
                    dto.getNombre(), dto.getAnioCarrera(), dto.getComision(),
                    Turno.valueOf(dto.getTurno()), carrera, dto.getAnioLectivo()
            );
            return CursoResponseDTO.fromEntity(cursoRepository.save(curso));
        } catch (IllegalArgumentException e) {
            throw new OperacionInvalidaException("Turno invalido: " + dto.getTurno() + ". Valores permitidos: MANIANA, TARDE, NOCHE");
        }
    }

    public CursoResponseDTO actualizar(Long id, CursoDTO dto) {
        Curso curso = buscarPorId(id);
        curso.setNombre(dto.getNombre());
        curso.setAnioCarrera(dto.getAnioCarrera());
        curso.setComision(dto.getComision());
        if (dto.getTurno() != null) {
            try {
                curso.setTurno(Turno.valueOf(dto.getTurno()));
            } catch (IllegalArgumentException e) {
                throw new OperacionInvalidaException("Turno invalido: " + dto.getTurno());
            }
        }
        curso.setAnioLectivo(dto.getAnioLectivo());

        if (dto.getIdCarrera() != null) {
            Carrera carrera = carreraRepository.findById(dto.getIdCarrera())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Carrera", dto.getIdCarrera()));
            curso.setCarrera(carrera);
        }

        return CursoResponseDTO.fromEntity(cursoRepository.save(curso));
    }

    public void eliminar(Long id) {
        Curso curso = buscarPorId(id);
        curso.setActivo(false);
        cursoRepository.save(curso);
    }
}
