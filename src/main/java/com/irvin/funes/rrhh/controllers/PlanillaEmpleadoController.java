package com.irvin.funes.rrhh.controllers;

import com.irvin.funes.rrhh.dtos.PlanillaEmpleadoDto;
import com.irvin.funes.rrhh.dtos.SolicitudesDiasLibresDto;
import com.irvin.funes.rrhh.exception.ResourceNotFoundException;
import com.irvin.funes.rrhh.models.*;
import com.irvin.funes.rrhh.repositories.*;
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

    //Crear planilla de un empleado
    @PostMapping("/planilla/crear/{id}/mes/{mes}/anio/{anio}")
    public ResponseEntity<?> crearPlanillaEmpleado(@PathVariable Long id, @PathVariable String mes, @PathVariable String anio) {
        Optional<Usuario> usuarioOptional = service.porId(id);

        if (!usuarioOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Usuario usuario = usuarioOptional.get();
        PlanillaEmpleadoDto planillaDto = new PlanillaEmpleadoDto();

        double salarioBase = Double.parseDouble(usuario.getSalario());
        double salarioDia = salarioBase / 30;
        double salarioHora = salarioDia / 8;
        double horasAusentes = 0; // Aquí actualizarás luego con el cálculo de ausencias en el mes y año
        double diasIncapacidad = 0; // Lo actualizarás al verificar los días de incapacidad

        // Llenar los datos básicos del empleado
        planillaDto.setNombreEmpleado(usuario.getNombre());
        planillaDto.setCargoEmpleado(usuario.getCargo());
        planillaDto.setDuiEmpleado(usuario.getDui());
        planillaDto.setSalarioBase(salarioBase);
        planillaDto.setSalarioDia(salarioDia);
        planillaDto.setMes(mes);
        planillaDto.setAnio(anio);

        // Definir las fechas de inicio y fin del mes
        planillaDto.setFechaInicio(mes + "/01/" + anio);
        planillaDto.setFechaFin(mes + "/30/" + anio);  // o ajustar según el mes

        double horasExtrasDiurnas = 0;

        // 1. Pago de horas extras diurnas
        ExtrasDiurnas extrasDiurnas = extrasDiurnasRepository.findByUsuarioIdAndMesAndAño(id, mes, anio);
        if (extrasDiurnas != null) {
            horasExtrasDiurnas = extrasDiurnas.getCantidad_horas();
        }
        double pagoHorasEDiurnas = salarioHora * 2 * horasExtrasDiurnas;
        planillaDto.setHorasEDiurnas(pagoHorasEDiurnas);

        // 2. Pago de horas extras nocturnas
        double horasExtrasNocturnas =0;
        ExtrasNocturnas extrasNocturnas = extrasNocturnasRepository.findByUsuarioIdAndMesAndAño(id, mes, anio);
        if (extrasNocturnas != null) {
            horasExtrasNocturnas = extrasNocturnas.getCantidad_horas();
        }
        double pagoHorasENocturnas = salarioHora * 2.5 * horasExtrasNocturnas;
        planillaDto.setHorasENocturnas(pagoHorasENocturnas);

        // 3. Pago por asueto trabajado
        double horasAsueto=0;
        if(asuetosTrabajadosRepository.findByUsuarioIdAndMesAndAño(id, mes, anio)!=null){
            horasAsueto = asuetosTrabajadosRepository.findByUsuarioIdAndMesAndAño(id, mes, anio).getCantidad_horas();
        }
        double pagoAsuetos = salarioHora * 2 * horasAsueto;
        planillaDto.setAsuetos(pagoAsuetos);

        VacacionesDiasUsuario vacaciones = vacacionesDiasUsuariosRepository.findByUsuarioIdAndMesAndAño(id, mes, anio);

        // 4. Pago de vacaciones (si aplica en el mes y año)
        if (vacaciones != null) {
            double pagoVacaciones = (salarioBase / 2) * 0.30;
            planillaDto.setVacaciones(pagoVacaciones);
        } else {
            planillaDto.setVacaciones(0);
        }

        // 5. Descuento por incapacidad (si es mayor a 3 días)

        if (incapacidadDiasUsuarioRepository.findByUsuarioIdAndMesAndAño(id, mes, anio) != null) {
            diasIncapacidad = incapacidadDiasUsuarioRepository.findByUsuarioIdAndMesAndAño(id, mes, anio).getCantidad_dias();
        }

        double descuentoIncapacidad = 0;
        if (diasIncapacidad > 3) {
            descuentoIncapacidad = (diasIncapacidad - 3) * salarioDia;
            planillaDto.setIncapacidades(descuentoIncapacidad);
        } else {
            planillaDto.setIncapacidades(0);
        }

        // 6. Descuento de ausencia
        if (ausenciaDiaUsuarioRepository.findByUsuarioIdAndMesAndAño(id, mes, anio) != null) {
            horasAusentes = ausenciaDiaUsuarioRepository.findByUsuarioIdAndMesAndAño(id, mes, anio).getCantidad_horas();
        }

        double descuentoAusencia = horasAusentes * salarioHora;
        planillaDto.setDiasAusentes(descuentoAusencia);

        // 7. Total devengado
        double totalDevengado = salarioBase + pagoHorasEDiurnas + pagoHorasENocturnas + pagoAsuetos - descuentoIncapacidad - descuentoAusencia;
        planillaDto.setTotalDevengado(totalDevengado);

        // 8. Descuento AFP
        double descuentoAfp = totalDevengado * 0.0725;
        planillaDto.setDescuetoAfp(descuentoAfp);

        // 9. Descuento ISSS
        double descuentoIsss = salarioBase > 1000 ? 1000 * 0.03 : totalDevengado * 0.03;
        planillaDto.setDescuentoIsss(descuentoIsss);

        // 10. Descuento de renta
        double descuentoRenta = salarioBase * 0.10;
        planillaDto.setDescuentoRenta(descuentoRenta);

        // 11. Total descuentos
        double totalDescuentos = descuentoAfp + descuentoIsss + descuentoRenta;
        planillaDto.setTotalDescuentos(totalDescuentos);

        // 12. Total líquido a pagar
        double liquidoPagar = totalDevengado - totalDescuentos;
        planillaDto.setLiquidoPagar(liquidoPagar);

        // Calcular días laborados
        double diasLaborados = 0;
        if (cargaLaboralDiurnaRepository.findByUsuarioIdAndMesAndAño(id, mes, anio) != null) {
             diasLaborados = (cargaLaboralDiurnaRepository.findByUsuarioIdAndMesAndAño(id, mes, anio).getCantidad_horas())/8;
             diasLaborados = diasLaborados - (horasAusentes/8);
        }
        planillaDto.setDiasLaborados(diasLaborados);

        // Guardar la planilla en la base de datos
        PlanillaEmpleado planilla = new PlanillaEmpleado();
        planilla.setHorasEDiurnas(planillaDto.getHorasEDiurnas());
        planilla.setHorasENocturnas(planillaDto.getHorasENocturnas());
        planilla.setNombreEmpleado(planillaDto.getNombreEmpleado());
        planilla.setCargoEmpleado(planillaDto.getCargoEmpleado());
        planilla.setDuiEmpleado(planillaDto.getDuiEmpleado());
        planilla.setFechaInicio(planillaDto.getFechaInicio());
        planilla.setFechaFin(planillaDto.getFechaFin());
        planilla.setSalarioBase(planillaDto.getSalarioBase());
        planilla.setSalarioDia(planillaDto.getSalarioDia());
        planilla.setDiasLaborados(planillaDto.getDiasLaborados());
        planilla.setDiasAusentes(planillaDto.getDiasAusentes());
        planilla.setIncapacidades(planillaDto.getIncapacidades());
        planilla.setVacaciones(planillaDto.getVacaciones());
        planilla.setAsuetos(planillaDto.getAsuetos());
        planilla.setTotalDevengado(planillaDto.getTotalDevengado());
        planilla.setDescuetoAfp(planillaDto.getDescuetoAfp());
        planilla.setDescuentoIsss(planillaDto.getDescuentoIsss());
        planilla.setDescuentoRenta(planillaDto.getDescuentoRenta());
        planilla.setTotalDescuentos(planillaDto.getTotalDescuentos());
        planilla.setLiquidoPagar(planillaDto.getLiquidoPagar());
        planilla.setMes(planillaDto.getMes());
        planilla.setAnio(planillaDto.getAnio());
        planilla.setUsuario(usuario);

        planillaEmpleadoRepository.save(planilla);

        return ResponseEntity.status(HttpStatus.CREATED).body(planilla);
    }


//CREAR TODAS LAS PLANILLAS DE TODOS LOS EMPLEADOS
    @PostMapping("/planillas/crear/mes/{mes}/anio/{anio}")
    public ResponseEntity<?> crearPlanillasParaTodos(@PathVariable String mes, @PathVariable String anio) {
        List<Usuario> usuarios = usuarioRepository.findAll();

        if (usuarios.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No hay usuarios en la base de datos.");
        }

        List<PlanillaEmpleado> planillasGeneradas = new ArrayList<>();

        for (Usuario usuario : usuarios) {
            PlanillaEmpleadoDto planillaDto = new PlanillaEmpleadoDto();

            double salarioBase = Double.parseDouble(usuario.getSalario());
            double salarioDia = salarioBase / 30;
            double salarioHora = salarioDia / 8;
            double horasAusentes = 0;
            double diasIncapacidad = 0;

            // Llenar datos básicos del empleado
            planillaDto.setNombreEmpleado(usuario.getNombre());
            planillaDto.setCargoEmpleado(usuario.getCargo());
            planillaDto.setDuiEmpleado(usuario.getDui());
            planillaDto.setSalarioBase(salarioBase);
            planillaDto.setSalarioDia(salarioDia);
            planillaDto.setMes(mes);
            planillaDto.setAnio(anio);

            // Definir las fechas de inicio y fin del mes
            planillaDto.setFechaInicio(mes + "/01/" + anio);
            planillaDto.setFechaFin(mes + "/30/" + anio);  // ajustar según el mes

            // 1. Pago de horas extras diurnas
            double horasExtrasDiurnas = 0;
            ExtrasDiurnas extrasDiurnas = extrasDiurnasRepository.findByUsuarioIdAndMesAndAño(usuario.getId(), mes, anio);
            if (extrasDiurnas != null) {
                horasExtrasDiurnas = extrasDiurnas.getCantidad_horas();
            }
            double pagoHorasEDiurnas = salarioHora * 2 * horasExtrasDiurnas;
            planillaDto.setHorasEDiurnas(pagoHorasEDiurnas);

            // 2. Pago de horas extras nocturnas
            double horasExtrasNocturnas =0;
            ExtrasNocturnas extrasNocturnas = extrasNocturnasRepository.findByUsuarioIdAndMesAndAño(usuario.getId(), mes, anio);
            if (extrasNocturnas != null) {
                horasExtrasNocturnas = extrasNocturnas.getCantidad_horas();
            }
            double pagoHorasENocturnas = salarioHora * 2.5 * horasExtrasNocturnas;
            planillaDto.setHorasENocturnas(pagoHorasENocturnas);

            // 3. Pago por asueto trabajado
            double horasAsueto=0;
            if(asuetosTrabajadosRepository.findByUsuarioIdAndMesAndAño(usuario.getId(), mes, anio)!=null){
                horasAsueto = asuetosTrabajadosRepository.findByUsuarioIdAndMesAndAño(usuario.getId(), mes, anio).getCantidad_horas();
            }
            double pagoAsuetos = salarioHora * 2 * horasAsueto;
            planillaDto.setAsuetos(pagoAsuetos);

            VacacionesDiasUsuario vacaciones = vacacionesDiasUsuariosRepository.findByUsuarioIdAndMesAndAño(usuario.getId(), mes, anio);

            // 4. Pago de vacaciones (si aplica en el mes y año)
            if (vacaciones != null) {
                double pagoVacaciones = (salarioBase / 2) * 0.30;
                planillaDto.setVacaciones(pagoVacaciones);
            } else {
                planillaDto.setVacaciones(0);
            }

            // 5. Descuento por incapacidad (si es mayor a 3 días)
            if (incapacidadDiasUsuarioRepository.findByUsuarioIdAndMesAndAño(usuario.getId(), mes, anio) != null) {
                diasIncapacidad = incapacidadDiasUsuarioRepository.findByUsuarioIdAndMesAndAño(usuario.getId(), mes, anio).getCantidad_dias();
            }

            double descuentoIncapacidad = 0;
            if (diasIncapacidad > 3) {
                descuentoIncapacidad = (diasIncapacidad - 3) * salarioDia;
                planillaDto.setIncapacidades(descuentoIncapacidad);
            } else {
                planillaDto.setIncapacidades(0);
            }

            // 6. Descuento de ausencia
            if (ausenciaDiaUsuarioRepository.findByUsuarioIdAndMesAndAño(usuario.getId(), mes, anio) != null) {
                horasAusentes = ausenciaDiaUsuarioRepository.findByUsuarioIdAndMesAndAño(usuario.getId(), mes, anio).getCantidad_horas();
            }

            double descuentoAusencia = horasAusentes * salarioHora;
            planillaDto.setDiasAusentes(horasAusentes);

            // 7. Total devengado
            double totalDevengado = salarioBase + pagoHorasEDiurnas + pagoHorasENocturnas + pagoAsuetos - descuentoIncapacidad - descuentoAusencia;
            planillaDto.setTotalDevengado(totalDevengado);

            // 8. Descuento AFP
            double descuentoAfp = totalDevengado * 0.0725;
            planillaDto.setDescuetoAfp(descuentoAfp);

            // 9. Descuento ISSS
            double descuentoIsss = salarioBase > 1000 ? 1000 * 0.03 : totalDevengado * 0.03;
            planillaDto.setDescuentoIsss(descuentoIsss);

            // 10. Descuento de renta
            double descuentoRenta = salarioBase * 0.10;
            planillaDto.setDescuentoRenta(descuentoRenta);

            // 11. Total descuentos
            double totalDescuentos = descuentoAfp + descuentoIsss + descuentoRenta;
            planillaDto.setTotalDescuentos(totalDescuentos);

            // 12. Total líquido a pagar
            double liquidoPagar = totalDevengado - totalDescuentos;
            planillaDto.setLiquidoPagar(liquidoPagar);

            // Calcular días laborados
            double diasLaborados = 0;
            if (cargaLaboralDiurnaRepository.findByUsuarioIdAndMesAndAño(usuario.getId(), mes, anio) != null) {
                diasLaborados = (cargaLaboralDiurnaRepository.findByUsuarioIdAndMesAndAño(usuario.getId(), mes, anio).getCantidad_horas())/8;
                diasLaborados = diasLaborados - (horasAusentes/8);
            }
            planillaDto.setDiasLaborados(diasLaborados);

            // Guardar la planilla en la base de datos
            PlanillaEmpleado planilla = new PlanillaEmpleado();
            planilla.setHorasEDiurnas(planillaDto.getHorasEDiurnas());
            planilla.setHorasENocturnas(planillaDto.getHorasENocturnas());
            planilla.setNombreEmpleado(planillaDto.getNombreEmpleado());
            planilla.setCargoEmpleado(planillaDto.getCargoEmpleado());
            planilla.setDuiEmpleado(planillaDto.getDuiEmpleado());
            planilla.setFechaInicio(planillaDto.getFechaInicio());
            planilla.setFechaFin(planillaDto.getFechaFin());
            planilla.setSalarioBase(planillaDto.getSalarioBase());
            planilla.setSalarioDia(planillaDto.getSalarioDia());
            planilla.setDiasLaborados(planillaDto.getDiasLaborados());
            planilla.setDiasAusentes(planillaDto.getDiasAusentes());
            planilla.setIncapacidades(planillaDto.getIncapacidades());
            planilla.setVacaciones(planillaDto.getVacaciones());
            planilla.setAsuetos(planillaDto.getAsuetos());
            planilla.setTotalDevengado(planillaDto.getTotalDevengado());
            planilla.setDescuetoAfp(planillaDto.getDescuetoAfp());
            planilla.setDescuentoIsss(planillaDto.getDescuentoIsss());
            planilla.setDescuentoRenta(planillaDto.getDescuentoRenta());
            planilla.setTotalDescuentos(planillaDto.getTotalDescuentos());
            planilla.setLiquidoPagar(planillaDto.getLiquidoPagar());
            planilla.setMes(planillaDto.getMes());
            planilla.setAnio(planillaDto.getAnio());
            planilla.setUsuario(usuario);

            planillaEmpleadoRepository.save(planilla);
            planillasGeneradas.add(planilla);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(planillasGeneradas);
    }



    //Traer todas las planillas
    @GetMapping("/planillas")
    public ResponseEntity<List<PlanillaEmpleado>> listaSolicitudes() {
        List<PlanillaEmpleado> planillaEmpleados = new ArrayList<>();
        planillaEmpleadoRepository.findAll().forEach(planillaEmpleados::add);

        if (planillaEmpleados.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(planillaEmpleados, HttpStatus.OK);
    }

    //Traer planilla de un usuario
    @GetMapping("/planillas/{id}")
    public ResponseEntity<List<PlanillaEmpleado>> listaSolicitudesUsuario(@PathVariable Long id) {
        List<PlanillaEmpleado> planillaEmpleados = new ArrayList<>();
        planillaEmpleadoRepository.findByUsuarioId(id).forEach(planillaEmpleados::add);

        if (planillaEmpleados.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(planillaEmpleados, HttpStatus.OK);
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
