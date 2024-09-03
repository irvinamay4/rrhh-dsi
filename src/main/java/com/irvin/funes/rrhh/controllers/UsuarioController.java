package com.irvin.funes.rrhh.controllers;

import com.irvin.funes.rrhh.models.*;
import com.irvin.funes.rrhh.repositories.RegistroHorasRepository;
import com.irvin.funes.rrhh.repositories.UsuarioRepository;
import com.irvin.funes.rrhh.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class UsuarioController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioService service;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RegistroHorasRepository registroHorasRepository;

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
    public ResponseEntity<?> crear(@Valid @RequestBody Usuario usuario, BindingResult result) {

        if (result.hasErrors()) {
            return validar(result);
        }

        if (!usuario.getEmail().isBlank() && service.existePorEmail(usuario.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("mensaje", "Ya existe un usuario con ese email"));
        }

        Set<RolesUsuario> roles = usuario.getRoles();

        try {
            // Llenar automáticamente los datos de la planilla
            PlanillaEmpleado planilla = usuario.getPlanillaEmpleado();
            if (planilla != null) {
                double deducciones = 0;
                double salario = Double.parseDouble(usuario.getSalario());

                planilla.setIssMes(salario * 0.075);
                planilla.setAfpMes(salario * 0.0875);
                planilla.setAguinaldo(salario);
                planilla.setHorasEDiurnas((((salario / 30) / 8) * 2) * usuario.getHorasDiurnas().getAgosto());
                planilla.setHorasENocturnas((((salario / 30) / 8) * 2.5) * usuario.getHorasDiurnas().getAgosto());

                deducciones += planilla.getIssMes();
                deducciones += planilla.getAfpMes();
                System.out.println("Deducciones que se le restaran al salario: " + deducciones);
                usuario.setSalario_neto(String.valueOf(salario - deducciones));

                usuario.setPlanillaEmpleado(planilla);
            }
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

            // Asignar los roles al usuario
            usuario.setRoles(roles);

            Usuario usuarioGuardado = service.guardar(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioGuardado);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("mensaje", "El formato del salario es incorrecto"));
        }
    }


    //Solo se cambia los datos base de usuario, no se actualiza ni la planilla ni las horas
    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@Valid @RequestBody Usuario usuario, BindingResult result, @PathVariable Long id) {

        if (result.hasErrors()) {
            return validar(result);
        }

        Optional<Usuario> o = service.porId(id);
        if (o.isPresent()) {
            Usuario usuarioDb = o.get();

            if (!usuario.getEmail().isBlank() &&
                    !usuario.getEmail().equalsIgnoreCase(usuarioDb.getEmail()) &&
                    service.porEmail(usuario.getEmail()).isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Collections
                                .singletonMap("mensaje", "Ya existe un usuario con ese email"));
            }

            usuarioDb.setNombre(usuario.getNombre());
            usuarioDb.setEmail(usuario.getEmail());
            usuarioDb.setPassword(usuario.getPassword());
            usuarioDb.setTelefono(usuario.getTelefono());
            usuarioDb.setDireccion(usuario.getDireccion());
            usuarioDb.setEdad(usuario.getEdad());
            usuarioDb.setDui(usuario.getDui());
            usuarioDb.setCuenta_planillera(usuario.getCuenta_planillera());
            usuarioDb.setCargo(usuario.getCargo());
            usuarioDb.setFecha_ingreso(usuario.getFecha_ingreso());
            usuarioDb.setSalario(usuario.getSalario());
            usuarioDb.setHorasDiurnas(usuario.getHorasDiurnas());
            usuarioDb.setHorasNocturnas(usuario.getHorasNocturnas());

            // Llenar automáticamente los datos de la planilla
            PlanillaEmpleado planilla = usuario.getPlanillaEmpleado();
            if (planilla != null) {
                double deducciones = 0;
                double salario = Double.parseDouble(usuario.getSalario());

                planilla.setIssMes(salario * 0.075);
                planilla.setAfpMes(salario * 0.0875);
                planilla.setAguinaldo(salario);
                planilla.setHorasEDiurnas((((salario / 30) / 8) * 2) * usuario.getHorasDiurnas().getAgosto()); //(Sueldo diario por hora * 2 ) horas diurnas
                planilla.setHorasENocturnas((((salario / 30) / 8) * 2.5) * usuario.getHorasDiurnas().getAgosto()); //(Sueldo diario por hora * 2.5 ) horas nocturnas

                deducciones += planilla.getIssMes();
                deducciones += planilla.getAfpMes();
                usuario.setSalario_neto(String.valueOf(salario - deducciones));
                usuarioDb.setSalario_neto(usuario.getSalario_neto());

                usuarioDb.setPlanillaEmpleado(planilla);
            }

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


    @PostMapping("/horas")
    public ResponseEntity<?> tiempo( @RequestBody RegistroHoras registroHoras, BindingResult result){

        if (result.hasErrors()){
            return validar(result);
        }


        return ResponseEntity.status(HttpStatus.CREATED).body(registroHorasRepository.save(registroHoras));
    }
    @GetMapping("/horas")
    public ResponseEntity<List<RegistroHoras>> obtenerTodasLasHoras() {
        List<RegistroHoras> horas = (List<RegistroHoras>) registroHorasRepository.findAll();
        return new ResponseEntity<>(horas, HttpStatus.OK);
    }


    private static ResponseEntity<Map<String, String>> validar(BindingResult result) {
        Map<String,String> errores = new HashMap<>();
        result.getFieldErrors().forEach(err->{
            errores.put(err.getField(),"El campo " + err.getField() + " " + err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errores);
    }
}