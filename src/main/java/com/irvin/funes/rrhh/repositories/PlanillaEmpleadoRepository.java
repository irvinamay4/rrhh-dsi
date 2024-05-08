package com.irvin.funes.rrhh.repositories;

import com.irvin.funes.rrhh.models.PlanillaEmpleado;
import jakarta.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;

public interface PlanillaEmpleadoRepository extends CrudRepository<PlanillaEmpleado,Long> {
    /*@Transactional
    void deleteById(long id);

    @Transactional
    void deleteByUsuarioId(long usuarioId);*/
}
