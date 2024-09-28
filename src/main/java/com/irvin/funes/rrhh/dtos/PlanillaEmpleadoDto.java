package com.irvin.funes.rrhh.dtos;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanillaEmpleadoDto {
    private Long id;

    private  double issMes;
    private  double afpMes;
    private  double horasEDiurnas;
    private  double horasENocturnas;
    private Long usuario_id;

    // Constructor
    public PlanillaEmpleadoDto(Long id, double issMes, double afpMes, double horasEDiurnas, double horasENocturnas, Long usuario_id) {
        this.id = id;
        this.issMes = issMes;
        this.afpMes = afpMes;
        this.horasEDiurnas = horasEDiurnas;
        this.horasENocturnas = horasENocturnas;
        this.usuario_id = usuario_id;
    }
}
