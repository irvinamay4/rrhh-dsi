package com.irvin.funes.rrhh.controllers;

import com.irvin.funes.rrhh.dtos.*;
import com.irvin.funes.rrhh.models.*;
import com.irvin.funes.rrhh.repositories.*;
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

    //REPOSITORIOS DE TABLAS DE HORAS Y DIAS
    @Autowired
    private SolicitudesDiasLibresRepository solicitudesDiasLibresRepository;

    @Autowired
    private AsuetosTrabajadosRepository asuetosTrabajadosRepository;

    @Autowired
    private CargaLaboralDiurnaRepository cargaLaboralDiurnaRepository;

    @Autowired
    private ExtrasDiurnasRepository extrasDiurnasRepository;

    @Autowired
    private ExtrasNocturnasRepository extrasNocturnasRepository;

    @Autowired
    private IncapacidadDiasUsuarioRepository incapacidadDiasUsuarioRepository;

    @Autowired
    private VacacionesDiasUsuariosRepository vacacionesDiasUsuariosRepository;

    @Autowired
    private AusenciaDiaUsuarioRepository ausenciaDiaUsuarioRepository;


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
        //double dias_descontados = usuario.getDias_descontados() * ((   (Double.parseDouble(usuario.getSalario())) / 30) / 8);

        try {
            // Llenar automáticamente los datos de la planilla
            /*PlanillaEmpleado planilla = usuario.getPlanillaEmpleado();
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
            }*/
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

            // Asignar los roles al usuario
            usuario.setRoles(roles);

            usuario.setEstado("Activo");

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
            usuarioDb.setEstado(usuario.getEstado());

            //double dias_descontados = usuario.getDias_descontados() * ((   (Double.parseDouble(usuario.getSalario())) / 30) / 8);

            //usuarioDb.setHoras(usuario.getHoras());
            //usuarioDb.setDias_descontados(usuario.getDias_descontados() );

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
            /*PlanillaEmpleado planilla = usuario.getPlanillaEmpleado();
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
            }*/

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

    //SOLICITUDES DE DIAS LIBRES
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



    //ASUETOS TRABAJADOS ************************************************************************************************
    @GetMapping("/asuetos-trabajados/consultar/usuario/{usuarioId}")
    public ResponseEntity<?> listarAsuetosTrabajadosPorUsuario(@PathVariable("usuarioId") Long usuarioId) {
        List<AsuetoTrabajadoDiasUsuario> asuetosTrabajado = asuetosTrabajadosRepository.findByUsuarioId(usuarioId);
        System.out.println("ENTROROOOOOOOOO");
        if (!asuetosTrabajado.isEmpty()) {
            System.out.println("ENTROROOOOOOOOO");
            return ResponseEntity.ok().body(asuetosTrabajado);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/asuetos-trabajados/crear/{id}")
    public ResponseEntity<?> crearAsuetoTrabajado(@Valid @RequestBody AsuetoTrabajadoDiasUsuario asuetoTrabajado, BindingResult result, @PathVariable Long id) {
        if (result.hasErrors()) {
            return validar(result);
        }

        Optional<Usuario> o = service.porId(id);
        if (o.isPresent()) {
            Usuario usuario = o.get();
            Set<AsuetoTrabajadoDiasUsuario> asuetosTrabajado = usuario.getAsuetoTrabajadoDiasUsuarios();

            // Buscar si ya existe un registro con el mismo mes y año
            Optional<AsuetoTrabajadoDiasUsuario> existingAsueto = asuetosTrabajado.stream()
                    .filter(a -> a.getMes().equals(asuetoTrabajado.getMes()) && a.getAño().equals(asuetoTrabajado.getAño()))
                    .findFirst();

            if (existingAsueto.isPresent()) {
                // Acumular horas si ya existe
                AsuetoTrabajadoDiasUsuario asuetoExistente = existingAsueto.get();
                asuetoExistente.setCantidad_horas(asuetoExistente.getCantidad_horas() + asuetoTrabajado.getCantidad_horas());
            } else {
                // Crear nuevo registro si no existe
                asuetoTrabajado.setUsuario(usuario);
                asuetosTrabajado.add(asuetoTrabajado);
            }

            usuario.setAsuetoTrabajadoDiasUsuarios(asuetosTrabajado);
            return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(usuario));
        }
        return ResponseEntity.notFound().build();
    }

    //ARREGLAR QUE NO TRAE EL ID_USUARIO... NO LO TRAE PORQUE ES UN CAMPO QUE CREA EN LA TABLA DIRECTAMENTE
    @GetMapping("/asuetos-trabajados")
    public ResponseEntity<List<AsuetosTrabajadosDto>> listaAsuetosTrabajados() {
        List<AsuetoTrabajadoDiasUsuario> asuetosTrabajados = new ArrayList<>();
        asuetosTrabajadosRepository.findAll().forEach(asuetosTrabajados::add);

        if (asuetosTrabajados.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // Convertir las entidades en DTO
        List<AsuetosTrabajadosDto> solicitudesDTO = new ArrayList<>();
        for (AsuetoTrabajadoDiasUsuario solicitud : asuetosTrabajados) {
            AsuetosTrabajadosDto dto = new AsuetosTrabajadosDto(
                    solicitud.getId(),
                    solicitud.getCantidad_horas(),
                    solicitud.getMes(),
                    solicitud.getAño(),
                    solicitud.getUsuario() != null ? solicitud.getUsuario().getId() : null  // Obtener usuario_id
            );
            solicitudesDTO.add(dto);
        }

        return new ResponseEntity<>(solicitudesDTO, HttpStatus.OK);
    }


    //CARGA LABORAL DIURNA ************************************************************************************************
    @GetMapping("/carga-laboral-diurna/consultar/usuario/{usuarioId}")
    public ResponseEntity<?> listarCargaLaboralDiurnaPorUsuario(@PathVariable("usuarioId") Long usuarioId) {
        List<CargaLaboralDiurnaUsuario> cargaLaboralDiurnaUsuarios = cargaLaboralDiurnaRepository.findByUsuarioId(usuarioId);
        System.out.println("ENTROROOOOOOOOO");
        if (!cargaLaboralDiurnaUsuarios.isEmpty()) {
            System.out.println("ENTROROOOOOOOOO");
            return ResponseEntity.ok().body(cargaLaboralDiurnaUsuarios);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/carga-laboral-diurna/crear/{id}")
    public ResponseEntity<?> crearCargaLaboralDiurna(@Valid @RequestBody CargaLaboralDiurnaUsuario cargaLaboral, BindingResult result, @PathVariable Long id) {
        if (result.hasErrors()) {
            return validar(result);
        }

        Optional<Usuario> o = service.porId(id);
        if (o.isPresent()) {
            Usuario usuario = o.get();
            Set<CargaLaboralDiurnaUsuario> cargaLaboralDiurnaUsuarios = usuario.getCargaLaboralDiurnaUsuarios();

            // Buscar si ya existe un registro con el mismo mes y año
            Optional<CargaLaboralDiurnaUsuario> existingAsueto = cargaLaboralDiurnaUsuarios.stream()
                    .filter(a -> a.getMes().equals(cargaLaboral.getMes()) && a.getAño().equals(cargaLaboral.getAño()))
                    .findFirst();

            if (existingAsueto.isPresent()) {
                // Acumular horas si ya existe
                CargaLaboralDiurnaUsuario asuetoExistente = existingAsueto.get();
                asuetoExistente.setCantidad_horas(asuetoExistente.getCantidad_horas() + cargaLaboral.getCantidad_horas());
            } else {
                // Crear nuevo registro si no existe
                cargaLaboral.setUsuario(usuario);
                cargaLaboralDiurnaUsuarios.add(cargaLaboral);
            }

            usuario.setCargaLaboralDiurnaUsuarios(cargaLaboralDiurnaUsuarios);
            return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(usuario));
        }
        return ResponseEntity.notFound().build();
    }

    //ARREGLAR QUE NO TRAE EL ID_USUARIO... NO LO TRAE PORQUE ES UN CAMPO QUE CREA EN LA TABLA DIRECTAMENTE
    @GetMapping("/carga-laboral-diurna")
    public ResponseEntity<List<CargaLaboralDiurnaDto>> listaCargaLaboralDiurna() {
        List<CargaLaboralDiurnaUsuario> cargaLaboralDiurnaUsuarios = new ArrayList<>();
        cargaLaboralDiurnaRepository.findAll().forEach(cargaLaboralDiurnaUsuarios::add);

        if (cargaLaboralDiurnaUsuarios.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // Convertir las entidades en DTO
        List<CargaLaboralDiurnaDto> solicitudesDTO = new ArrayList<>();
        for (CargaLaboralDiurnaUsuario solicitud : cargaLaboralDiurnaUsuarios) {
            CargaLaboralDiurnaDto dto = new CargaLaboralDiurnaDto(
                    solicitud.getId(),
                    solicitud.getCantidad_horas(),
                    solicitud.getMes(),
                    solicitud.getAño(),
                    solicitud.getUsuario() != null ? solicitud.getUsuario().getId() : null  // Obtener usuario_id
            );
            solicitudesDTO.add(dto);
        }

        return new ResponseEntity<>(solicitudesDTO, HttpStatus.OK);
    }


    //EXTRAS DIURNAS ************************************************************************************************
    @GetMapping("/extras-diurnas/consultar/usuario/{usuarioId}")
    public ResponseEntity<?> listarExtrasDiurnasPorUsuario(@PathVariable("usuarioId") Long usuarioId) {
        List<ExtrasDiurnas> extrasDiurnas = extrasDiurnasRepository.findByUsuarioId(usuarioId);
        System.out.println("ENTROROOOOOOOOO");
        if (!extrasDiurnas.isEmpty()) {
            System.out.println("ENTROROOOOOOOOO");
            return ResponseEntity.ok().body(extrasDiurnas);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/extras-diurnas/crear/{id}")
    public ResponseEntity<?> crearExtrasDiurnas(@Valid @RequestBody ExtrasDiurnas extrasDiurnas, BindingResult result, @PathVariable Long id) {
        if (result.hasErrors()) {
            return validar(result);
        }

        Optional<Usuario> o = service.porId(id);
        if (o.isPresent()) {
            Usuario usuario = o.get();
            Set<ExtrasDiurnas> extrasDiurnasSet = usuario.getExtrasDiurnas();

            // Buscar si ya existe un registro con el mismo mes y año
            Optional<ExtrasDiurnas> existingHoras = extrasDiurnasSet.stream()
                    .filter(a -> a.getMes().equals(extrasDiurnas.getMes()) && a.getAño().equals(extrasDiurnas.getAño()))
                    .findFirst();

            if (existingHoras.isPresent()) {
                // Acumular horas si ya existe
                ExtrasDiurnas extrasDiurnasAcumuado = existingHoras.get();
                extrasDiurnasAcumuado.setCantidad_horas(extrasDiurnasAcumuado.getCantidad_horas() + extrasDiurnas.getCantidad_horas());
            } else {
                // Crear nuevo registro si no existe
                extrasDiurnas.setUsuario(usuario);
                extrasDiurnasSet.add(extrasDiurnas);
            }

            usuario.setExtrasDiurnas(extrasDiurnasSet);
            return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(usuario));
        }
        return ResponseEntity.notFound().build();
    }

    //ARREGLAR QUE NO TRAE EL ID_USUARIO... NO LO TRAE PORQUE ES UN CAMPO QUE CREA EN LA TABLA DIRECTAMENTE
    @GetMapping("/extras-diurnas")
    public ResponseEntity<List<ExtrasDiurnasDto>> listaExtrasDiurnas() {
        List<ExtrasDiurnas> extrasDiurnas = new ArrayList<>();
        extrasDiurnasRepository.findAll().forEach(extrasDiurnas::add);

        if (extrasDiurnas.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // Convertir las entidades en DTO
        List<ExtrasDiurnasDto> solicitudesDTO = new ArrayList<>();
        for (ExtrasDiurnas horas : extrasDiurnas) {
            ExtrasDiurnasDto dto = new ExtrasDiurnasDto(
                    horas.getId(),
                    horas.getCantidad_horas(),
                    horas.getMes(),
                    horas.getAño(),
                    horas.getUsuario() != null ? horas.getUsuario().getId() : null  // Obtener usuario_id
            );
            solicitudesDTO.add(dto);
        }

        return new ResponseEntity<>(solicitudesDTO, HttpStatus.OK);
    }


    //EXTRAS NOCTURNAS ************************************************************************************************
    @GetMapping("/extras-nocturnas/consultar/usuario/{usuarioId}")
    public ResponseEntity<?> listarExtrasnNocturnasPorUsuario(@PathVariable("usuarioId") Long usuarioId) {
        List<ExtrasNocturnas> extrasNocturnas = extrasNocturnasRepository.findByUsuarioId(usuarioId);
        System.out.println("ENTROROOOOOOOOO");
        if (!extrasNocturnas.isEmpty()) {
            System.out.println("ENTROROOOOOOOOO");
            return ResponseEntity.ok().body(extrasNocturnas);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/extras-nocturnas/crear/{id}")
    public ResponseEntity<?> crearExtrasNocturnas(@Valid @RequestBody ExtrasNocturnas extrasNocturnas, BindingResult result, @PathVariable Long id) {
        if (result.hasErrors()) {
            return validar(result);
        }

        Optional<Usuario> o = service.porId(id);
        if (o.isPresent()) {
            Usuario usuario = o.get();
            Set<ExtrasNocturnas> extrasNocturnasSet = usuario.getExtrasNocturnas();

            // Buscar si ya existe un registro con el mismo mes y año
            Optional<ExtrasNocturnas> existingHoras = extrasNocturnasSet.stream()
                    .filter(a -> a.getMes().equals(extrasNocturnas.getMes()) && a.getAño().equals(extrasNocturnas.getAño()))
                    .findFirst();

            if (existingHoras.isPresent()) {
                // Acumular horas si ya existe
                ExtrasNocturnas extrasNocturnasAcumulado = existingHoras.get();
                extrasNocturnasAcumulado.setCantidad_horas(extrasNocturnasAcumulado.getCantidad_horas() + extrasNocturnas.getCantidad_horas());
            } else {
                // Crear nuevo registro si no existe
                extrasNocturnas.setUsuario(usuario);
                extrasNocturnasSet.add(extrasNocturnas);
            }

            usuario.setExtrasNocturnas(extrasNocturnasSet);
            return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(usuario));
        }
        return ResponseEntity.notFound().build();
    }

    //ARREGLAR QUE NO TRAE EL ID_USUARIO... NO LO TRAE PORQUE ES UN CAMPO QUE CREA EN LA TABLA DIRECTAMENTE
    @GetMapping("/extras-nocturnas")
    public ResponseEntity<List<ExtrasNocturnasDto>> listaExtrasNocturnas() {
        List<ExtrasNocturnas> extrasNocturnas = new ArrayList<>();
        extrasNocturnasRepository.findAll().forEach(extrasNocturnas::add);

        if (extrasNocturnas.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // Convertir las entidades en DTO
        List<ExtrasNocturnasDto> solicitudesDTO = new ArrayList<>();
        for (ExtrasNocturnas horas : extrasNocturnas) {
            ExtrasNocturnasDto dto = new ExtrasNocturnasDto(
                    horas.getId(),
                    horas.getCantidad_horas(),
                    horas.getMes(),
                    horas.getAño(),
                    horas.getUsuario() != null ? horas.getUsuario().getId() : null  // Obtener usuario_id
            );
            solicitudesDTO.add(dto);
        }

        return new ResponseEntity<>(solicitudesDTO, HttpStatus.OK);
    }


    //Incapacidad Dias Usuario ************************************************************************************************
    @GetMapping("/incapadidad-dias-usuario/consultar/usuario/{usuarioId}")
    public ResponseEntity<?> listarIncapacidadDiasPorUsuario(@PathVariable("usuarioId") Long usuarioId) {
        List<IncapacidadDiasUsuario> incapacidadDiasUsuarios = incapacidadDiasUsuarioRepository.findByUsuarioId(usuarioId);
        System.out.println("ENTROROOOOOOOOO");
        if (!incapacidadDiasUsuarios.isEmpty()) {
            System.out.println("ENTROROOOOOOOOO");
            return ResponseEntity.ok().body(incapacidadDiasUsuarios);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/incapadidad-dias-usuario/crear/{id}")
    public ResponseEntity<?> crearIncapacidadDias(@Valid @RequestBody IncapacidadDiasUsuario incapacidadDiasUsuario, BindingResult result, @PathVariable Long id) {
        if (result.hasErrors()) {
            return validar(result);
        }

        Optional<Usuario> o = service.porId(id);
        if (o.isPresent()) {
            Usuario usuario = o.get();
            Set<IncapacidadDiasUsuario> incapacidadDiasUsuarioSet = usuario.getIncapacidadDiasUsuarios();

            // Buscar si ya existe un registro con el mismo mes y año
            Optional<IncapacidadDiasUsuario> existingDias = incapacidadDiasUsuarioSet.stream()
                    .filter(a -> a.getMes().equals(incapacidadDiasUsuario.getMes()) && a.getAño().equals(incapacidadDiasUsuario.getAño()))
                    .findFirst();

            if (existingDias.isPresent()) {
                // Acumular dias si ya existe
                IncapacidadDiasUsuario incapacidadDiasAcumulado = existingDias.get();
                incapacidadDiasAcumulado.setCantidad_dias(incapacidadDiasAcumulado.getCantidad_dias() + incapacidadDiasUsuario.getCantidad_dias());
            } else {
                // Crear nuevo registro si no existe
                incapacidadDiasUsuario.setUsuario(usuario);
                incapacidadDiasUsuarioSet.add(incapacidadDiasUsuario);
            }

            usuario.setIncapacidadDiasUsuarios(incapacidadDiasUsuarioSet);
            return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(usuario));
        }
        return ResponseEntity.notFound().build();
    }

    //ARREGLAR QUE NO TRAE EL ID_USUARIO... NO LO TRAE PORQUE ES UN CAMPO QUE CREA EN LA TABLA DIRECTAMENTE
    @GetMapping("/incapadidad-dias-usuario")
    public ResponseEntity<List<IncapacidadDiasUsuarioDto>> listaIncapacidadDias() {
        List<IncapacidadDiasUsuario> extrasNocturnas = new ArrayList<>();
        incapacidadDiasUsuarioRepository.findAll().forEach(extrasNocturnas::add);

        if (extrasNocturnas.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // Convertir las entidades en DTO
        List<IncapacidadDiasUsuarioDto> solicitudesDTO = new ArrayList<>();
        for (IncapacidadDiasUsuario dias : extrasNocturnas) {
            IncapacidadDiasUsuarioDto dto = new IncapacidadDiasUsuarioDto(
                    dias.getId(),
                    dias.getCantidad_dias(),
                    dias.getMes(),
                    dias.getAño(),
                    dias.getUsuario() != null ? dias.getUsuario().getId() : null  // Obtener usuario_id
            );
            solicitudesDTO.add(dto);
        }

        return new ResponseEntity<>(solicitudesDTO, HttpStatus.OK);
    }


    //Vacaciones Dias Usuario ************************************************************************************************
    @GetMapping("/vacaciones-dias-usuario/consultar/usuario/{usuarioId}")
    public ResponseEntity<?> listarVacacionesDiasPorUsuario(@PathVariable("usuarioId") Long usuarioId) {
        List<VacacionesDiasUsuario> vacacionesDiasUsuarios = vacacionesDiasUsuariosRepository.findByUsuarioId(usuarioId);
        System.out.println("ENTROROOOOOOOOO");
        if (!vacacionesDiasUsuarios.isEmpty()) {
            System.out.println("ENTROROOOOOOOOO");
            return ResponseEntity.ok().body(vacacionesDiasUsuarios);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/vacaciones-dias-usuario/crear/{id}")
    public ResponseEntity<?> crearVacacionesDias(@Valid @RequestBody VacacionesDiasUsuario vacacionesDiasUsuario, BindingResult result, @PathVariable Long id) {
        if (result.hasErrors()) {
            return validar(result);
        }

        Optional<Usuario> o = service.porId(id);
        if (o.isPresent()) {
            Usuario usuario = o.get();
            Set<VacacionesDiasUsuario> vacacionesDiasUsuarioSet = usuario.getVacacionesDiasUsuarios();

            // Crear nuevo registro si no existe
            vacacionesDiasUsuario.setUsuario(usuario);
            vacacionesDiasUsuarioSet.add(vacacionesDiasUsuario);

            usuario.setVacacionesDiasUsuarios(vacacionesDiasUsuarioSet);
            return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(usuario));
        }
        return ResponseEntity.notFound().build();
    }

    //ARREGLAR QUE NO TRAE EL ID_USUARIO... NO LO TRAE PORQUE ES UN CAMPO QUE CREA EN LA TABLA DIRECTAMENTE
    @GetMapping("/vacaciones-dias-usuario")
    public ResponseEntity<List<VacacionesDiasUsuarioDto>> listaVacacionesDias() {
        List<VacacionesDiasUsuario> vacacionesDiasUsuarios = new ArrayList<>();
        vacacionesDiasUsuariosRepository.findAll().forEach(vacacionesDiasUsuarios::add);

        if (vacacionesDiasUsuarios.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // Convertir las entidades en DTO
        List<VacacionesDiasUsuarioDto> solicitudesDTO = new ArrayList<>();
        for (VacacionesDiasUsuario dias : vacacionesDiasUsuarios) {
            VacacionesDiasUsuarioDto dto = new VacacionesDiasUsuarioDto(
                    dias.getId(),
                    dias.getFecha_inicio(),
                    dias.getFecha_fin(),
                    dias.getCantidad_dias(),
                    dias.getMes(),
                    dias.getAño(),
                    dias.getUsuario() != null ? dias.getUsuario().getId() : null  // Obtener usuario_id
            );
            solicitudesDTO.add(dto);
        }

        return new ResponseEntity<>(solicitudesDTO, HttpStatus.OK);
    }

    //Ausencias Dias Usuario ************************************************************************************************
    @GetMapping("/ausencia-dias-usuario/consultar/usuario/{usuarioId}")
    public ResponseEntity<?> listarAusenciasDiasPorUsuario(@PathVariable("usuarioId") Long usuarioId) {
        List<AusenciaDiaUsuario> ausenciaDiaUsuarios = ausenciaDiaUsuarioRepository.findByUsuarioId(usuarioId);
        System.out.println("ENTROROOOOOOOOO");
        if (!ausenciaDiaUsuarios.isEmpty()) {
            System.out.println("ENTROROOOOOOOOO");
            return ResponseEntity.ok().body(ausenciaDiaUsuarios);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/ausencia-dias-usuario/crear/{id}")
    public ResponseEntity<?> crearAusenciaDias(@Valid @RequestBody AusenciaDiaUsuario ausenciaDiaUsuario, BindingResult result, @PathVariable Long id) {
        if (result.hasErrors()) {
            return validar(result);
        }

        Optional<Usuario> o = service.porId(id);
        if (o.isPresent()) {
            Usuario usuario = o.get();
            Set<AusenciaDiaUsuario> ausenciaDiaUsuarioSet = usuario.getAusenciaDiaUsuarios();

            // Buscar si ya existe un registro con el mismo mes y año
            Optional<AusenciaDiaUsuario> existingDias = ausenciaDiaUsuarioSet.stream()
                    .filter(a -> a.getMes().equals(ausenciaDiaUsuario.getMes()) && a.getAño().equals(ausenciaDiaUsuario.getAño()))
                    .findFirst();

            if (existingDias.isPresent()) {
                // Acumular dias si ya existe
                AusenciaDiaUsuario incapacidadDiasAcumulado = existingDias.get();
                incapacidadDiasAcumulado.setCantidad_horas(incapacidadDiasAcumulado.getCantidad_horas() + ausenciaDiaUsuario.getCantidad_horas());
            } else {
                // Crear nuevo registro si no existe
                ausenciaDiaUsuario.setUsuario(usuario);
                ausenciaDiaUsuarioSet.add(ausenciaDiaUsuario);
            }

            usuario.setAusenciaDiaUsuarios(ausenciaDiaUsuarioSet);
            return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(usuario));
        }
        return ResponseEntity.notFound().build();
    }


    //ARREGLAR QUE NO TRAE EL ID_USUARIO... NO LO TRAE PORQUE ES UN CAMPO QUE CREA EN LA TABLA DIRECTAMENTE
    @GetMapping("/ausencia-dias-usuario")
    public ResponseEntity<List<AusenciaDiaUsuarioDto>> listaAusenciasDias() {
        List<AusenciaDiaUsuario> ausenciaDiaUsuarios = new ArrayList<AusenciaDiaUsuario>();
        ausenciaDiaUsuarioRepository.findAll().forEach(ausenciaDiaUsuarios::add);

        if (ausenciaDiaUsuarios.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // Convertir las entidades en DTO
        List<AusenciaDiaUsuarioDto> solicitudesDTO = new ArrayList<>();
        for (AusenciaDiaUsuario dias : ausenciaDiaUsuarios) {
            AusenciaDiaUsuarioDto dto = new AusenciaDiaUsuarioDto(
                    dias.getId(),
                    dias.getCantidad_horas(),
                    dias.getMes(),
                    dias.getAño(),
                    dias.getUsuario() != null ? dias.getUsuario().getId() : null  // Obtener usuario_id
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