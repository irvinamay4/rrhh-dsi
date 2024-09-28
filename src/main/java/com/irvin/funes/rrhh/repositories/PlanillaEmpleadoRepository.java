package com.irvin.funes.rrhh.repositories;

import com.irvin.funes.rrhh.models.PlanillaEmpleado;
import jakarta.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanillaEmpleadoRepository extends CrudRepository<PlanillaEmpleado,Long> {
    /*@Transactional
    void deleteById(long id);

    @Transactional
    void deleteByUsuarioId(long usuarioId);*/

    List<PlanillaEmpleado> findByUsuarioId(Long usuarioId);
}
