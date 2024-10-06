package com.irvin.funes.rrhh.repositories;

import com.irvin.funes.rrhh.models.AsuetoTrabajadoDiasUsuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AsuetosTrabajadosRepository extends CrudRepository<AsuetoTrabajadoDiasUsuario,Long> {

    // Método para encontrar asuetos trabajados por ID del usuario
    List<AsuetoTrabajadoDiasUsuario> findByUsuarioId(Long usuarioId);
}
