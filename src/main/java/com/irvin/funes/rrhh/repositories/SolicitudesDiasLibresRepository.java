package com.irvin.funes.rrhh.repositories;

import com.irvin.funes.rrhh.models.SolicitudesDiasLibres;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface SolicitudesDiasLibresRepository extends CrudRepository<SolicitudesDiasLibres,Long> {

    // Método para encontrar solicitudes de días libres por ID del usuario
    List<SolicitudesDiasLibres> findByUsuarioId(Long usuarioId);

}
