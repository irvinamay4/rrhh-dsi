package com.irvin.funes.rrhh.repositories;

import com.irvin.funes.rrhh.models.AsuetoTrabajadoDiasUsuario;
import com.irvin.funes.rrhh.models.AusenciaDiaUsuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AusenciaDiaUsuarioRepository extends CrudRepository<AusenciaDiaUsuario, Long> {
    // Método para encontrar ausencia por ID del usuario
    List<AusenciaDiaUsuario> findByUsuarioId(Long usuarioId);
    AusenciaDiaUsuario findByUsuarioIdAndMesAndAño(Long usuarioId, String mes, String año);

}
