package com.irvin.funes.rrhh.repositories;

import com.irvin.funes.rrhh.models.ExtrasNocturnas;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ExtrasNocturnasRepository extends CrudRepository<ExtrasNocturnas,Long> {
    // Método para encontrar extras nocturnas por ID del usuario
    List<ExtrasNocturnas> findByUsuarioId(Long usuarioId);
    ExtrasNocturnas findByUsuarioIdAndMesAndAño(Long usuarioId, String mes, String año);

}
