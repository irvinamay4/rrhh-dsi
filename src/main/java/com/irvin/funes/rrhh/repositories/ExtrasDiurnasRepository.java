package com.irvin.funes.rrhh.repositories;

import com.irvin.funes.rrhh.models.ExtrasDiurnas;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ExtrasDiurnasRepository extends CrudRepository<ExtrasDiurnas,Long>{
    // Método para encontrar extras diurnas por ID del usuario
    List<ExtrasDiurnas> findByUsuarioId(Long usuarioId);
    ExtrasDiurnas findByUsuarioIdAndMesAndAño(Long usuarioId, String mes, String año);

}
