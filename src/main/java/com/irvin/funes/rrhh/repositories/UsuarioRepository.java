package com.irvin.funes.rrhh.repositories;


import com.irvin.funes.rrhh.models.Usuario;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email); //Una forma automatica

    @Query("select u from Usuario u where u.email = ?1")
    Optional<Usuario> porEmail(String email); //Forma por query

    boolean existsByEmail(String email);//Forma por palabra clave, es mas eficiente porque solo valida si existe, no busca todo el objeto
}
