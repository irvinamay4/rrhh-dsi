package com.irvin.funes.rrhh.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@OneToOne(cascade = CascadeType.ALL,orphanRemoval = true)
    //@OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Set<PlanillaEmpleado> planillaEmpleado;

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

    private String estado;

    private String sexo;

    @OneToOne(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "diurnas_id", referencedColumnName = "id") //Para que se agregue una columna usuario_id a la tabla HorasDiurnas
    private HorasDiurnas horasDiurnas;

    @OneToOne(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "nocturnas_id", referencedColumnName = "id") //Para que se agregue una columna usuario_id a la tabla HorasNocturnas
    private HorasNocturnas horasNocturnas;

    @ManyToMany(fetch = FetchType.EAGER, targetEntity = RolesUsuario.class, cascade = CascadeType.PERSIST)//eager para que al consultar un user me traiga todos los roles de una vez, lazy seria 1 por 1
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RolesUsuario> roles;

    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Set<SolicitudesDiasLibres> solicitudesDiasLibres;

    //todo: tablas de deducciones varias
    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Set<IncapacidadDiasUsuario> incapacidadDiasUsuarios;

    //YA MAPEADA
    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Set<AsuetoTrabajadoDiasUsuario> asuetoTrabajadoDiasUsuarios;

    //YA MAPEADA
    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Set<CargaLaboralDiurnaUsuario> cargaLaboralDiurnaUsuarios;

    //YA MAPEADA
    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Set<ExtrasDiurnas> extrasDiurnas;

    //YA MAPEADA
    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Set<ExtrasNocturnas> extrasNocturnas;

    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Set<AusenciaDiaUsuario> ausenciaDiaUsuarios;

    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Set<VacacionesDiasUsuario> vacacionesDiasUsuarios;



    public Usuario(Long id, Set<PlanillaEmpleado> planillaEmpleado, HorasDiurnas horasDiurnas, HorasNocturnas horasNocturnas,
                   String nombre, String email, String password, String telefono, String direccion, String edad, String dui,
                   String cuenta_planillera, String cargo, String fecha_ingreso, String salario, String salario_neto, String estado, String sexo,
                   Set<RolesUsuario> roles, Set<SolicitudesDiasLibres> solicitudesDiasLibres, Set<AsuetoTrabajadoDiasUsuario> asuetoTrabajadoDiasUsuarios,
                   Set<CargaLaboralDiurnaUsuario> cargaLaboralDiurnaUsuarios, Set<ExtrasDiurnas> extrasDiurnas, Set<ExtrasNocturnas> extrasNocturnas) {
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
        this.estado = estado;
        this.sexo = sexo;
        this.roles = roles;
        this.solicitudesDiasLibres = solicitudesDiasLibres;
        this.asuetoTrabajadoDiasUsuarios = asuetoTrabajadoDiasUsuarios;
        this.cargaLaboralDiurnaUsuarios = cargaLaboralDiurnaUsuarios;
        this.extrasDiurnas = extrasDiurnas;
        this.extrasNocturnas = extrasNocturnas;
    }

    public Usuario() {
    }


}