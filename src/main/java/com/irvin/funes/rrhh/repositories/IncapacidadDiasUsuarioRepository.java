package com.irvin.funes.rrhh.repositories;


import com.irvin.funes.rrhh.models.IncapacidadDiasUsuario;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IncapacidadDiasUsuarioRepository extends CrudRepository<IncapacidadDiasUsuario,Long> {
    List<IncapacidadDiasUsuario> findByUsuarioId(Long usuarioId);
    IncapacidadDiasUsuario findByUsuarioIdAndMesAndAño(Long usuarioId, String mes, String año);
}
