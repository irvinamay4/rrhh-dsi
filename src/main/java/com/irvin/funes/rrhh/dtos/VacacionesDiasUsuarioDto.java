package com.irvin.funes.rrhh.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VacacionesDiasUsuarioDto {
    private Long id;
    private String fecha_inicio;
    private String fecha_fin;
    private double cantidad_dias;
    private String mes;
    private String año;
    private Long usuario_id;

    public VacacionesDiasUsuarioDto(Long id, String fecha_inicio, String fecha_fin, double cantidad_dias, String mes, String año, Long usuario_id) {
        this.id = id;
        this.fecha_inicio = fecha_inicio;
        this.fecha_fin = fecha_fin;
        this.cantidad_dias = cantidad_dias;
        this.mes = mes;
        this.año = año;
        this.usuario_id = usuario_id;
    }
}
