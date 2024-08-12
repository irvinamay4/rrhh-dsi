package com.irvin.funes.rrhh.services;

import com.irvin.funes.rrhh.models.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    List<Usuario> listar();
    Optional<Usuario> porId(Long id);
    Usuario guardar(Usuario usuario);
    void eliminar(Long id);

    Optional<Usuario> porEmail(String email); //primeras 2 formas
    boolean existePorEmail(String email);
    List<Usuario> listarPorIds(Iterable<Long> ids);
}
