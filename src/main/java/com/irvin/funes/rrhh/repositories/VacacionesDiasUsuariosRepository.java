package com.irvin.funes.rrhh.repositories;

import com.irvin.funes.rrhh.models.VacacionesDiasUsuario;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface VacacionesDiasUsuariosRepository extends CrudRepository<VacacionesDiasUsuario,Long> {
    List<VacacionesDiasUsuario> findByUsuarioId(Long usuarioId);
    VacacionesDiasUsuario findByUsuarioIdAndMesAndAño(Long usuarioId, String mes, String año);
}
