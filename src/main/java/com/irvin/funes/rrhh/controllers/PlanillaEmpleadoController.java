package com.irvin.funes.rrhh.controllers;

import com.irvin.funes.rrhh.exception.ResourceNotFoundException;
import com.irvin.funes.rrhh.models.PlanillaEmpleado;
import com.irvin.funes.rrhh.models.Usuario;
import com.irvin.funes.rrhh.repositories.PlanillaEmpleadoRepository;
import com.irvin.funes.rrhh.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PlanillaEmpleadoController {

    @Autowired
    private PlanillaEmpleadoRepository planillaEmpleadoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

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
}
