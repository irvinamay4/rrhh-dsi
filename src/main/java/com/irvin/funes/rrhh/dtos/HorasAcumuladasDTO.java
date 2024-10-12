package com.irvin.funes.rrhh.dtos;

import com.irvin.funes.rrhh.dtos.AsuetosTrabajadosDto;
import com.irvin.funes.rrhh.dtos.CargaLaboralDiurnaDto;
import com.irvin.funes.rrhh.dtos.ExtrasDiurnasDto;
import com.irvin.funes.rrhh.dtos.ExtrasNocturnasDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HorasAcumuladasDTO {
    private AsuetosTrabajadosDto asuetoTrabajadoDiasUsuario;
    private CargaLaboralDiurnaDto cargaLaboralDiurna;
    private ExtrasDiurnasDto extrasDiurnas;
    private ExtrasNocturnasDto extrasNocturnas;

    // Constructor
    public HorasAcumuladasDTO(AsuetosTrabajadosDto asuetoTrabajadoDiasUsuario,
                              CargaLaboralDiurnaDto cargaLaboralDiurna,
                              ExtrasDiurnasDto extrasDiurnas,
                              ExtrasNocturnasDto extrasNocturnas) {
        this.asuetoTrabajadoDiasUsuario = asuetoTrabajadoDiasUsuario;
        this.cargaLaboralDiurna = cargaLaboralDiurna;
        this.extrasDiurnas = extrasDiurnas;
        this.extrasNocturnas = extrasNocturnas;
    }
    }


