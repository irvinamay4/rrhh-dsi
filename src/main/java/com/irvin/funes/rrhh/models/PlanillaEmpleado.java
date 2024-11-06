package com.irvin.funes.rrhh.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "empleado_planilla")
public class PlanillaEmpleado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private  double horasEDiurnas;//
    private  double horasENocturnas;//
    private String nombreEmpleado;
    private String cargoEmpleado;
    private String duiEmpleado;
    private String fechaInicio;
    private String fechaFin;
    private double salarioBase;
    private double salarioDia;
    private double diasLaborados;
    private double diasAusentes;//
    private double incapacidades;//
    private double vacaciones;//
    private double asuetos;//
    private double totalDevengado;//
    private double descuetoAfp;//
    private double descuentoIsss;//
    private double descuentoRenta;//
    private double totalDescuentos;//
    private double liquidoPagar;//
    private String mes;
    private String anio;

    //agregar horas y dias descontados...

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonIgnore // Esto evitará que el usuario completo aparezca en el JSON
    private Usuario usuario;


}

