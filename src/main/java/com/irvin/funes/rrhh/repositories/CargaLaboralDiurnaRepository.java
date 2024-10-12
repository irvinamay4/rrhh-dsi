package com.irvin.funes.rrhh.repositories;

import com.irvin.funes.rrhh.models.CargaLaboralDiurnaUsuario;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CargaLaboralDiurnaRepository extends CrudRepository<CargaLaboralDiurnaUsuario,Long> {
    // Método para encontrar carga laboral diurna por ID del usuario
    List<CargaLaboralDiurnaUsuario> findByUsuarioId(Long usuarioId);
    CargaLaboralDiurnaUsuario findByUsuarioIdAndMesAndAño(Long usuarioId, String mes, String año);

}
