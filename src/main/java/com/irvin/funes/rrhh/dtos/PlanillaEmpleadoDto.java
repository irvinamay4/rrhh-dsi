package com.irvin.funes.rrhh.dtos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanillaEmpleadoDto {
    private Long id;

    private  double horasEDiurnas;
    private  double horasENocturnas;
    private String nombreEmpleado;
    private String cargoEmpleado;
    private String duiEmpleado;
    private String fechaInicio;
    private String fechaFin;
    private double salarioBase;
    private double salarioDia;
    private double diasLaborados;
    private double diasAusentes;
    private double incapacidades;
    private double vacaciones;
    private double asuetos;
    private double totalDevengado;
    private double descuetoAfp;
    private double descuentoIsss;
    private double descuentoRenta;
    private double totalDescuentos;
    private double liquidoPagar;
    private String mes;
    private String anio;

}
