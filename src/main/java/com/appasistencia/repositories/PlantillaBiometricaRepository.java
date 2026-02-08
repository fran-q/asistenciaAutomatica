package com.appasistencia.repositories;

import com.appasistencia.models.PlantillaBiometrica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlantillaBiometricaRepository extends JpaRepository<PlantillaBiometrica, Long> {
    List<PlantillaBiometrica> findByActivoTrue();
    List<PlantillaBiometrica> findByUsuarioIdUsuarioAndActivoTrue(Long idUsuario);
}
