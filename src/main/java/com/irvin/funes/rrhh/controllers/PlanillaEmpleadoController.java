package com.irvin.funes.rrhh.controllers;

import com.irvin.funes.rrhh.dtos.PlanillaEmpleadoDto;
import com.irvin.funes.rrhh.dtos.SolicitudesDiasLibresDto;
import com.irvin.funes.rrhh.exception.ResourceNotFoundException;
import com.irvin.funes.rrhh.models.PlanillaEmpleado;
import com.irvin.funes.rrhh.models.SolicitudesDiasLibres;
import com.irvin.funes.rrhh.models.Usuario;
import com.irvin.funes.rrhh.repositories.PlanillaEmpleadoRepository;
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
public class PlanillaEmpleadoController {

    @Autowired
    private PlanillaEmpleadoRepository planillaEmpleadoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService service;




    @PostMapping("/planilla/crear/{id}")
    public ResponseEntity<?> crdearSolicitud(@Valid @RequestBody PlanillaEmpleado planilla, BindingResult result, @PathVariable Long id) {
        if (result.hasErrors()) {
            return validar(result);
        }

        Optional<Usuario> o = service.porId(id);
        if (o.isPresent()) {
            Usuario usuario = o.get();
            Set<PlanillaEmpleado> planillas = usuario.getPlanillaEmpleado(); //trae la coleccion de solicitudes del user
            planilla.setUsuario(usuario); //setea el usuario en la solicitud
            planillas.add(planilla); //adiciona una nueva solicitud
            usuario.setPlanillaEmpleado(planillas);//setea la coleccion ya con la nueva agregada
            return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(usuario));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/planillas")
    public ResponseEntity<List<PlanillaEmpleadoDto>> listaSolicitudes() {
        List<PlanillaEmpleado> planillaEmpleados = new ArrayList<>();
        planillaEmpleadoRepository.findAll().forEach(planillaEmpleados::add);

        if (planillaEmpleados.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // Convertir las entidades en DTO
        List<PlanillaEmpleadoDto> planillaEmpleadoDtos = new ArrayList<>();
        for (PlanillaEmpleado planilla : planillaEmpleados) {
            PlanillaEmpleadoDto dto = new PlanillaEmpleadoDto(
                    planilla.getId(),
                    planilla.getDescuentoIsss(),
                    planilla.getDescuetoAfp(),
                    planilla.getHorasEDiurnas(),
                    planilla.getHorasENocturnas(),
                    planilla.getUsuario() != null ? planilla.getUsuario().getId() : null  // Obtener usuario_id
            );
            planillaEmpleadoDtos.add(dto);
        }

        return new ResponseEntity<>(planillaEmpleadoDtos, HttpStatus.OK);
    }







    //A partir de aca controladores de prueba
    @GetMapping({ "/planilla/{id}", "/usuarios/{id}/planillas" })
    public ResponseEntity<PlanillaEmpleado> getDetailsById(@PathVariable(value = "id") Long id) {
        PlanillaEmpleado planillaEmpleado = planillaEmpleadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Tutorial Details with id = " + id));

        return new ResponseEntity<>(planillaEmpleado, HttpStatus.OK);
    }

    @PostMapping("/usuarios/{usuarioId}/planilla")
    public ResponseEntity<PlanillaEmpleado> createDetails(@PathVariable(value = "usuarioId") Long usuarioId,
                                                         @RequestBody PlanillaEmpleado planillaEmpleado) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro usuario con id = " + usuarioId));

        //planillaEmpleado.setCreatedOn(new java.util.Date());
        //planillaEmpleado.setUsuario(usuario);
        PlanillaEmpleado planilla = planillaEmpleadoRepository.save(planillaEmpleado);

        return new ResponseEntity<>(planilla, HttpStatus.CREATED);
    }

    private static ResponseEntity<Map<String, String>> validar(BindingResult result) {
        Map<String,String> errores = new HashMap<>();
        result.getFieldErrors().forEach(err->{
            errores.put(err.getField(),"El campo " + err.getField() + " " + err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errores);
    }

}
