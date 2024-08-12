package com.irvin.funes.rrhh.models;

import jakarta.persistence.*;

@Entity
@Table(name = "horas_diurnas")
public class HorasDiurnas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double enero;
    private double febrero;
    private double marzo;
    private double abril;
    private double mayo;
    private double junio;
    private double julio;
    private double agosto;
    private double septiembre;
    private double octubre;
    private double noviembre;
    private double diciembre;

    public HorasDiurnas(Long id, double enero, double febrero, double marzo, double abril, double mayo, double junio, double julio, double agosto, double septiembre, double octubre, double noviembre, double diciembre) {
        this.id = id;
        this.enero = enero;
        this.febrero = febrero;
        this.marzo = marzo;
        this.abril = abril;
        this.mayo = mayo;
        this.junio = junio;
        this.julio = julio;
        this.agosto = agosto;
        this.septiembre = septiembre;
        this.octubre = octubre;
        this.noviembre = noviembre;
        this.diciembre = diciembre;
    }

    public HorasDiurnas() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getEnero() {
        return enero;
    }

    public void setEnero(double enero) {
        this.enero = enero;
    }

    public double getFebrero() {
        return febrero;
    }

    public void setFebrero(double febrero) {
        this.febrero = febrero;
    }

    public double getMarzo() {
        return marzo;
    }

    public void setMarzo(double marzo) {
        this.marzo = marzo;
    }

    public double getAbril() {
        return abril;
    }

    public void setAbril(double abril) {
        this.abril = abril;
    }

    public double getMayo() {
        return mayo;
    }

    public void setMayo(double mayo) {
        this.mayo = mayo;
    }

    public double getJunio() {
        return junio;
    }

    public void setJunio(double junio) {
        this.junio = junio;
    }

    public double getJulio() {
        return julio;
    }

    public void setJulio(double julio) {
        this.julio = julio;
    }

    public double getAgosto() {
        return agosto;
    }

    public void setAgosto(double agosto) {
        this.agosto = agosto;
    }

    public double getSeptiembre() {
        return septiembre;
    }

    public void setSeptiembre(double septiembre) {
        this.septiembre = septiembre;
    }

    public double getOctubre() {
        return octubre;
    }

    public void setOctubre(double octubre) {
        this.octubre = octubre;
    }

    public double getNoviembre() {
        return noviembre;
    }

    public void setNoviembre(double noviembre) {
        this.noviembre = noviembre;
    }

    public double getDiciembre() {
        return diciembre;
    }

    public void setDiciembre(double diciembre) {
        this.diciembre = diciembre;
    }
}
