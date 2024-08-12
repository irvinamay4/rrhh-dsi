package com.irvin.funes.rrhh.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@OneToOne(cascade = CascadeType.ALL,orphanRemoval = true)
    //@OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @OneToOne(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "planilla_id", referencedColumnName = "id") //Para que se agregue una columna usuario_id a la tabla PlanillaEmpleado
    private PlanillaEmpleado planillaEmpleado;

    @NotEmpty(message = "El campo nombre no puede ser vacio")
    private String nombre;

    @NotEmpty(message = "El campo email no puede ser vacio")
    @Email
    @Column(unique = true)
    private String email;

    @NotBlank
    private  String password;

    @NotBlank
    private  String telefono;

    @NotBlank
    private  String direccion;

    @NotBlank
    private  String edad;

    @NotBlank
    private  String dui;

    @NotBlank
    private  String cuenta_planillera;

    @NotBlank
    private  String cargo;

    @NotBlank
    private  String fecha_ingreso;

    @NotBlank
    private  String salario;

    @NotBlank
    private  String salario_neto;

    @OneToOne(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "diurnas_id", referencedColumnName = "id") //Para que se agregue una columna usuario_id a la tabla HorasDiurnas
    private HorasDiurnas horasDiurnas;

    @OneToOne(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "nocturnas_id", referencedColumnName = "id") //Para que se agregue una columna usuario_id a la tabla HorasNocturnas
    private HorasNocturnas horasNocturnas;

    public Usuario(Long id, PlanillaEmpleado planillaEmpleado, HorasDiurnas horasDiurnas, HorasNocturnas horasNocturnas, String nombre, String email, String password, String telefono, String direccion, String edad, String dui, String cuenta_planillera, String cargo, String fecha_ingreso, String salario, String salario_neto) {
        this.id = id;
        this.planillaEmpleado = planillaEmpleado;
        this.horasDiurnas = horasDiurnas;
        this.horasNocturnas = horasNocturnas;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.telefono = telefono;
        this.direccion = direccion;
        this.edad = edad;
        this.dui = dui;
        this.cuenta_planillera = cuenta_planillera;
        this.cargo = cargo;
        this.fecha_ingreso = fecha_ingreso;
        this.salario = salario;
        this.salario_neto = salario_neto;
    }

    public Usuario() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEdad() {
        return edad;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    public String getDui() {
        return dui;
    }

    public void setDui(String dui) {
        this.dui = dui;
    }

    public String getCuenta_planillera() {
        return cuenta_planillera;
    }

    public void setCuenta_planillera(String cuenta_planillera) {
        this.cuenta_planillera = cuenta_planillera;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getFecha_ingreso() {
        return fecha_ingreso;
    }

    public void setFecha_ingreso(String fecha_ingreso) {
        this.fecha_ingreso = fecha_ingreso;
    }

    public String getSalario() {
        return salario;
    }

    public void setSalario(String salario) {
        this.salario = salario;
    }

    public String getSalario_neto() {
        return salario_neto;
    }

    public void setSalario_neto(String salario_neto) {
        this.salario_neto = salario_neto;
    }

    public PlanillaEmpleado getPlanillaEmpleado() {
        return planillaEmpleado;
    }

    public void setPlanillaEmpleado(PlanillaEmpleado planillaEmpleado) {
        this.planillaEmpleado = planillaEmpleado;
    }

    public HorasDiurnas getHorasDiurnas() {
        return horasDiurnas;
    }

    public void setHorasDiurnas(HorasDiurnas horasDiurnas) {
        this.horasDiurnas = horasDiurnas;
    }

    public HorasNocturnas getHorasNocturnas() {
        return horasNocturnas;
    }

    public void setHorasNocturnas(HorasNocturnas horasNocturnas) {
        this.horasNocturnas = horasNocturnas;
    }
}