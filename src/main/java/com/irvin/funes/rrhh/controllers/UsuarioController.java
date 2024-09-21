package com.irvin.funes.rrhh.controllers;

import com.irvin.funes.rrhh.dtos.SolicitudesDiasLibresDto;
import com.irvin.funes.rrhh.models.*;
import com.irvin.funes.rrhh.repositories.SolicitudesDiasLibresRepository;
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

@RestController
public class UsuarioController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioService service;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SolicitudesDiasLibresRepository solicitudesDiasLibresRepository;


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
        double dias_descontados = usuario.getDias_descontados() * ((   (Double.parseDouble(usuario.getSalario())) / 30) / 8);

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
                deducciones += dias_descontados;
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
    @PutMapping("modificar/{id}")
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

            double dias_descontados = usuario.getDias_descontados() * ((   (Double.parseDouble(usuario.getSalario())) / 30) / 8);

            usuarioDb.setHoras(usuario.getHoras());
            usuarioDb.setDias_descontados(usuario.getDias_descontados() );

            //horas:
            double diurnas = (((   (Double.parseDouble(usuario.getSalario())) / 30) / 8) * 2);

            usuarioDb.getHorasDiurnas().setEnero(usuario.getHorasDiurnas().getEnero() * diurnas);
            usuarioDb.getHorasDiurnas().setFebrero(usuario.getHorasDiurnas().getFebrero() * diurnas);
            usuarioDb.getHorasDiurnas().setMarzo(usuario.getHorasDiurnas().getMarzo() * diurnas);
            usuarioDb.getHorasDiurnas().setAbril(usuario.getHorasDiurnas().getAbril() * diurnas);
            usuarioDb.getHorasDiurnas().setMayo(usuario.getHorasDiurnas().getMayo() * diurnas);
            usuarioDb.getHorasDiurnas().setJunio(usuario.getHorasDiurnas().getJunio() * diurnas);
            usuarioDb.getHorasDiurnas().setJulio(usuario.getHorasDiurnas().getJulio() * diurnas);
            usuarioDb.getHorasDiurnas().setAgosto(usuario.getHorasDiurnas().getAgosto() * diurnas);
            usuarioDb.getHorasDiurnas().setSeptiembre(usuario.getHorasDiurnas().getSeptiembre() * diurnas);
            usuarioDb.getHorasDiurnas().setOctubre(usuario.getHorasDiurnas().getOctubre() * diurnas);
            usuarioDb.getHorasDiurnas().setNoviembre(usuario.getHorasDiurnas().getNoviembre() * diurnas);
            usuarioDb.getHorasDiurnas().setDiciembre(usuario.getHorasDiurnas().getDiciembre() * diurnas);

            double nocturnas = (((   (Double.parseDouble(usuario.getSalario())) / 30) / 8) * 2.5);

            // Horas nocturnas:
            usuarioDb.getHorasNocturnas().setEnero(usuario.getHorasNocturnas().getEnero() * nocturnas);
            usuarioDb.getHorasNocturnas().setFebrero(usuario.getHorasNocturnas().getFebrero() * nocturnas);
            usuarioDb.getHorasNocturnas().setMarzo(usuario.getHorasNocturnas().getMarzo() * nocturnas);
            usuarioDb.getHorasNocturnas().setAbril(usuario.getHorasNocturnas().getAbril() * nocturnas);
            usuarioDb.getHorasNocturnas().setMayo(usuario.getHorasNocturnas().getMayo() * nocturnas);
            usuarioDb.getHorasNocturnas().setJunio(usuario.getHorasNocturnas().getJunio() * nocturnas);
            usuarioDb.getHorasNocturnas().setJulio(usuario.getHorasNocturnas().getJulio() * nocturnas);
            usuarioDb.getHorasNocturnas().setAgosto(usuario.getHorasNocturnas().getAgosto() * nocturnas);
            usuarioDb.getHorasNocturnas().setSeptiembre(usuario.getHorasNocturnas().getSeptiembre() * nocturnas);
            usuarioDb.getHorasNocturnas().setOctubre(usuario.getHorasNocturnas().getOctubre() * nocturnas);
            usuarioDb.getHorasNocturnas().setNoviembre(usuario.getHorasNocturnas().getNoviembre() * nocturnas);
            usuarioDb.getHorasNocturnas().setDiciembre(usuario.getHorasNocturnas().getDiciembre() * nocturnas);



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
                deducciones += dias_descontados;
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

    @GetMapping("/sdiaslibres/consultar/usuario/{usuarioId}")
    public ResponseEntity<?> listarSolicitudesPorUsuario(@PathVariable("usuarioId") Long usuarioId) {
        List<SolicitudesDiasLibres> solicitudes = solicitudesDiasLibresRepository.findByUsuarioId(usuarioId);
        System.out.println("ENTROROOOOOOOOO");
        if (!solicitudes.isEmpty()) {
            System.out.println("ENTROROOOOOOOOO");
            return ResponseEntity.ok().body(solicitudes);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/sdiaslibres/crear/{id}")
    public ResponseEntity<?> crearSolicitud(@Valid @RequestBody SolicitudesDiasLibres solicitud, BindingResult result, @PathVariable Long id) {
        if (result.hasErrors()) {
            return validar(result);
        }

        Optional<Usuario> o = service.porId(id);
        if (o.isPresent()) {
            Usuario usuario = o.get();
            Set<SolicitudesDiasLibres> solicitudes = usuario.getSolicitudesDiasLibres(); //trae la coleccion de solicitudes del user
            solicitud.setUsuario(usuario); //setea el usuario en la solicitud
            solicitudes.add(solicitud); //adiciona una nueva solicitud
            usuario.setSolicitudesDiasLibres(solicitudes);//setea la coleccion ya con la nueva agregada
            return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(usuario));
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/sdiaslibres/{id}")
    public ResponseEntity<?> actualizarEstadoSolicitud(@RequestBody Map<String, String> estadoUpdate, @PathVariable Long id) {
        Optional<SolicitudesDiasLibres> o = solicitudesDiasLibresRepository.findById(id);

        if (o.isPresent()) {
            SolicitudesDiasLibres solicitudDb = o.get();

            // Extraemos el valor de "estado" del JSON
            String nuevoEstado = estadoUpdate.get("estado");
            if (nuevoEstado != null) {
                solicitudDb.setEstado(nuevoEstado);  // Actualizamos solo el campo "estado"
                solicitudesDiasLibresRepository.save(solicitudDb);
                return ResponseEntity.ok(solicitudDb);  // Devolvemos la solicitud actualizada
            } else {
                return ResponseEntity.badRequest().body("El campo 'estado' es requerido");
            }
        }

        return ResponseEntity.notFound().build();
    }

    //ARREGLAR QUE NO TRAE EL ID_USUARIO... NO LO TRAE PORQUE ES UN CAMPO QUE CREA EN LA TABLA DIRECTAMENTE
    @GetMapping("/solicitudes")
    public ResponseEntity<List<SolicitudesDiasLibresDto>> listaSolicitudes() {
        List<SolicitudesDiasLibres> solicitudesDiasLibres = new ArrayList<>();
        solicitudesDiasLibresRepository.findAll().forEach(solicitudesDiasLibres::add);

        if (solicitudesDiasLibres.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // Convertir las entidades en DTO
        List<SolicitudesDiasLibresDto> solicitudesDTO = new ArrayList<>();
        for (SolicitudesDiasLibres solicitud : solicitudesDiasLibres) {
            SolicitudesDiasLibresDto dto = new SolicitudesDiasLibresDto(
                    solicitud.getId(),
                    solicitud.getFecha_solicitud(),
                    solicitud.getFecha_inicio(),
                    solicitud.getFecha_fin(),
                    solicitud.getCantidad_dias(),
                    solicitud.getMes(),
                    solicitud.getAño(),
                    solicitud.getCausa(),
                    solicitud.getEstado(),
                    solicitud.getUsuario() != null ? solicitud.getUsuario().getId() : null  // Obtener usuario_id
            );
            solicitudesDTO.add(dto);
        }

        return new ResponseEntity<>(solicitudesDTO, HttpStatus.OK);
    }


    private static ResponseEntity<Map<String, String>> validar(BindingResult result) {
        Map<String,String> errores = new HashMap<>();
        result.getFieldErrors().forEach(err->{
            errores.put(err.getField(),"El campo " + err.getField() + " " + err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errores);
    }
}