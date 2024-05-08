package com.irvin.funes.rrhh.controllers;

import com.irvin.funes.rrhh.models.PlanillaEmpleado;
import com.irvin.funes.rrhh.models.Usuario;
import com.irvin.funes.rrhh.repositories.UsuarioRepository;
import com.irvin.funes.rrhh.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class UsuarioController {
    @Autowired
    private UsuarioService service;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public List<Usuario> listar(){
        return service.listar();
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> getAllTutorials() {
        List<Usuario> usuarios = new ArrayList<Usuario>();

            usuarioRepository.findAll().forEach(usuarios::add);

        if (usuarios.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    @GetMapping("/usuarios{id}")
    public ResponseEntity<?> detalle(@PathVariable Long id){
        Optional<Usuario> usuarioOptional = service.porId(id);//atajo ctrl alt v

        if (usuarioOptional.isPresent()){
            return ResponseEntity.ok().body(usuarioOptional.get());
        }
        return ResponseEntity.notFound().build();
    }

    /*@PostMapping("/crear")
    public Usuario crear( @RequestBody Usuario usuario){

        PlanillaEmpleado planillaEmpleado = usuario.getPlanillaEmpleado();
        System.out.println("**********************************************");
        System.out.println(planillaEmpleado);


        Usuario user = usuarioRepository.save(usuario);
        return user;
    }*/

    @PostMapping("/crear")
    public ResponseEntity<?> crear(@Valid @RequestBody Usuario usuario, BindingResult result){

        if (result.hasErrors()){
            return validar(result);
        }

        if (!usuario.getEmail().isBlank() && service.existePorEmail(usuario.getEmail())){//service.porEmail(usuario.getEmail()).isPresent()){
            return ResponseEntity.badRequest()
                    .body(Collections
                            .singletonMap("mensaje","Ya existe un usuario con ese email"));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(usuario));
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@Valid @RequestBody Usuario usuario,BindingResult result, @PathVariable Long id){

        if (result.hasErrors()){
            return validar(result);
        }

        Optional<Usuario> o = service.porId(id);
        if (o.isPresent()){
            Usuario usuarioDb = o.get();

            if (!usuario.getEmail().isBlank() &&
                    !usuario.getEmail().equalsIgnoreCase(usuarioDb.getEmail()) &&
                    service.porEmail(usuario.getEmail()).isPresent()){
                return ResponseEntity.badRequest()
                        .body(Collections
                                .singletonMap("mensaje","Ya existe un usuario con ese email"));
            }

            usuarioDb.setNombre(usuario.getNombre());
            usuarioDb.setEmail(usuario.getEmail());
            usuarioDb.setPassword(usuario.getPassword());

            return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(usuarioDb));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id){
        Optional<Usuario> o = service.porId(id);
        if (o.isPresent()){
            service.eliminar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }


    private static ResponseEntity<Map<String, String>> validar(BindingResult result) {
        Map<String,String> errores = new HashMap<>();
        result.getFieldErrors().forEach(err->{
            errores.put(err.getField(),"El campo " + err.getField() + " " + err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errores);
    }
}