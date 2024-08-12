package com.irvin.funes.rrhh.models;

import jakarta.persistence.*;

@Entity
@Table(name = "empleado_planilla")
public class PlanillaEmpleado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private  double issMes;
    private  double afpMes;
    private  double aguinaldo;
    private  double horasEDiurnas;
    private  double horasENocturnas;

    public PlanillaEmpleado(Long id, double issMes, double afpMes, double aguinaldo, double horasEDiurnas, double horasENocturnas) {
        this.id = id;
        this.issMes = issMes;
        this.afpMes = afpMes;
        this.aguinaldo = aguinaldo;
        this.horasEDiurnas = horasEDiurnas;
        this.horasENocturnas = horasENocturnas;
    }

    public PlanillaEmpleado() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

   /* public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }*/

    public double getIssMes() {
        return issMes;
    }

    public void setIssMes(double issMes) {
        this.issMes = issMes;
    }

    public double getAfpMes() {
        return afpMes;
    }

    public void setAfpMes(double afpMes) {
        this.afpMes = afpMes;
    }

    public double getAguinaldo() {
        return aguinaldo;
    }

    public void setAguinaldo(double aguinaldo) {
        this.aguinaldo = aguinaldo;
    }

    public double getHorasEDiurnas() {
        return horasEDiurnas;
    }

    public void setHorasEDiurnas(double horasEDiurnas) {
        this.horasEDiurnas = horasEDiurnas;
    }

    public double getHorasENocturnas() {
        return horasENocturnas;
    }

    public void setHorasENocturnas(double horasENocturnas) {
        this.horasENocturnas = horasENocturnas;
    }

    @Override
    public String toString() {
        return "PlanillaEmpleado{" +
                "ISS_MES=" + issMes +
                ", AFP_MES=" + afpMes +
                ", AGUINALDO=" + aguinaldo +
                ", HORAS_E_DIURNAS=" + horasEDiurnas +
                ", HORAS_E_NOCTURNAS=" + horasENocturnas +
                '}';
    }
}

