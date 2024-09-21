package com.irvin.funes.rrhh.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SolicitudesDiasLibresDto {
    private Long id;
    private String fecha_solicitud;
    private String fecha_inicio;
    private String fecha_fin;
    private int cantidad_dias;
    private String mes;
    private String año;
    private String causa;
    private String estado;
    private Long usuario_id;

    // Constructor
    public SolicitudesDiasLibresDto(Long id, String fecha_solicitud, String fecha_inicio, String fecha_fin,
                                    int cantidad_dias, String mes, String año, String causa, String estado, Long usuario_id) {
        this.id = id;
        this.fecha_solicitud = fecha_solicitud;
        this.fecha_inicio = fecha_inicio;
        this.fecha_fin = fecha_fin;
        this.cantidad_dias = cantidad_dias;
        this.mes = mes;
        this.año = año;
        this.causa = causa;
        this.estado = estado;
        this.usuario_id = usuario_id;
    }

}

