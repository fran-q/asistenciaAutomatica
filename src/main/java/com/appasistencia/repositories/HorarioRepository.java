package com.appasistencia.repositories;

import com.appasistencia.models.DiaSemana;
import com.appasistencia.models.Horario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HorarioRepository extends JpaRepository<Horario, Long> {
    List<Horario> findByActivoTrue();
    List<Horario> findByAsignacionIdAsignacionAndActivoTrue(Long idAsignacion);
    List<Horario> findByDiaSemanaAndActivoTrue(DiaSemana diaSemana);
}
