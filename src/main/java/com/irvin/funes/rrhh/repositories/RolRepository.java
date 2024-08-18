package com.irvin.funes.rrhh.repositories;

import com.irvin.funes.rrhh.models.RolesUsuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends CrudRepository<RolesUsuario, Long> {
}
