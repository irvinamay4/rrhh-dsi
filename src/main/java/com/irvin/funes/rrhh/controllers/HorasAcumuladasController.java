package com.irvin.funes.rrhh.controllers;

import com.irvin.funes.rrhh.dtos.*;
import com.irvin.funes.rrhh.models.*;
import com.irvin.funes.rrhh.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class HorasAcumuladasController {

    @Autowired
    private AsuetosTrabajadosRepository asuetosTrabajadosRepository;

    @Autowired
    private CargaLaboralDiurnaRepository cargaLaboralDiurnaRepository;

    @Autowired
    private ExtrasDiurnasRepository extrasDiurnasRepository;

    @Autowired
    private ExtrasNocturnasRepository extrasNocturnasRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    //HORAS ACUMULADAS POR USUARIO
    @GetMapping("/usuario/{usuarioId}/mes/{mes}/año/{año}")
    public ResponseEntity<HorasAcumuladasDTO> obtenerHorasAcumuladas(
            @PathVariable Long usuarioId,
            @PathVariable String mes,
            @PathVariable String año) {

        // Obtener las horas de cada tabla filtrando por usuarioId, mes y año
        AsuetoTrabajadoDiasUsuario asuetoTrabajado = asuetosTrabajadosRepository
                .findByUsuarioIdAndMesAndAño(usuarioId, mes, año);

        CargaLaboralDiurnaUsuario cargaLaboralDiurna = cargaLaboralDiurnaRepository
                .findByUsuarioIdAndMesAndAño(usuarioId, mes, año);

        ExtrasDiurnas extrasDiurnas = extrasDiurnasRepository
                .findByUsuarioIdAndMesAndAño(usuarioId, mes, año);

        ExtrasNocturnas extrasNocturnas = extrasNocturnasRepository
                .findByUsuarioIdAndMesAndAño(usuarioId, mes, año);

        // Convertir a DTOs para incluir solo los datos necesarios
        AsuetosTrabajadosDto asuetoTrabajadoDto = (asuetoTrabajado != null) ?
                new AsuetosTrabajadosDto(asuetoTrabajado.getId(), asuetoTrabajado.getCantidad_horas(),
                        asuetoTrabajado.getMes(), asuetoTrabajado.getAño(),
                        asuetoTrabajado.getUsuario().getId()) : null;

        CargaLaboralDiurnaDto cargaLaboralDiurnaDto = (cargaLaboralDiurna != null) ?
                new CargaLaboralDiurnaDto(cargaLaboralDiurna.getId(), cargaLaboralDiurna.getCantidad_horas(),
                        cargaLaboralDiurna.getMes(), cargaLaboralDiurna.getAño(),
                        cargaLaboralDiurna.getUsuario().getId()) : null;

        ExtrasDiurnasDto extrasDiurnasDto = (extrasDiurnas != null) ?
                new ExtrasDiurnasDto(extrasDiurnas.getId(), extrasDiurnas.getCantidad_horas(),
                        extrasDiurnas.getMes(), extrasDiurnas.getAño(),
                        extrasDiurnas.getUsuario().getId()) : null;

        ExtrasNocturnasDto extrasNocturnasDto = (extrasNocturnas != null) ?
                new ExtrasNocturnasDto(extrasNocturnas.getId(), extrasNocturnas.getCantidad_horas(),
                        extrasNocturnas.getMes(), extrasNocturnas.getAño(),
                        extrasNocturnas.getUsuario().getId()) : null;

        // Crear el DTO que agrupe los resultados
        HorasAcumuladasDTO horasAcumuladas = new HorasAcumuladasDTO(
                asuetoTrabajadoDto,
                cargaLaboralDiurnaDto,
                extrasDiurnasDto,
                extrasNocturnasDto
        );

        return ResponseEntity.ok(horasAcumuladas);
    }

//LISTA DE HORAS ACUMULADAS POR TODOS LOS USUARIOS
        @GetMapping("/mes/{mes}/año/{año}")
        public ResponseEntity<List<HorasAcumuladasDTO>> obtenerHorasAcumuladasPorTodosLosUsuarios(
                @PathVariable String mes,
                @PathVariable String año) {

            // Obtener todos los usuarios
            List<Usuario> usuarios = (List<Usuario>) usuarioRepository.findAll();

            // Lista para acumular los DTOs
            List<HorasAcumuladasDTO> horasAcumuladasList = new ArrayList<>();

            // Para cada usuario, obtener las horas de las 4 tablas
            for (Usuario usuario : usuarios) {
                // Obtener horas de cada tabla para el usuario y mes/año dado
                AsuetoTrabajadoDiasUsuario asuetoTrabajado = asuetosTrabajadosRepository
                        .findByUsuarioIdAndMesAndAño(usuario.getId(), mes, año);

                CargaLaboralDiurnaUsuario cargaLaboralDiurna = cargaLaboralDiurnaRepository
                        .findByUsuarioIdAndMesAndAño(usuario.getId(), mes, año);

                ExtrasDiurnas extrasDiurnas = extrasDiurnasRepository
                        .findByUsuarioIdAndMesAndAño(usuario.getId(), mes, año);

                ExtrasNocturnas extrasNocturnas = extrasNocturnasRepository
                        .findByUsuarioIdAndMesAndAño(usuario.getId(), mes, año);

                // Convertir a DTOs
                AsuetosTrabajadosDto asuetoTrabajadoDto = (asuetoTrabajado != null) ?
                        new AsuetosTrabajadosDto(asuetoTrabajado.getId(), asuetoTrabajado.getCantidad_horas(),
                                asuetoTrabajado.getMes(), asuetoTrabajado.getAño(),
                                asuetoTrabajado.getUsuario().getId()) : null;

                CargaLaboralDiurnaDto cargaLaboralDiurnaDto = (cargaLaboralDiurna != null) ?
                        new CargaLaboralDiurnaDto(cargaLaboralDiurna.getId(), cargaLaboralDiurna.getCantidad_horas(),
                                cargaLaboralDiurna.getMes(), cargaLaboralDiurna.getAño(),
                                cargaLaboralDiurna.getUsuario().getId()) : null;

                ExtrasDiurnasDto extrasDiurnasDto = (extrasDiurnas != null) ?
                        new ExtrasDiurnasDto(extrasDiurnas.getId(), extrasDiurnas.getCantidad_horas(),
                                extrasDiurnas.getMes(), extrasDiurnas.getAño(),
                                extrasDiurnas.getUsuario().getId()) : null;

                ExtrasNocturnasDto extrasNocturnasDto = (extrasNocturnas != null) ?
                        new ExtrasNocturnasDto(extrasNocturnas.getId(), extrasNocturnas.getCantidad_horas(),
                                extrasNocturnas.getMes(), extrasNocturnas.getAño(),
                                extrasNocturnas.getUsuario().getId()) : null;

                // Crear el DTO que agrupe los resultados de este usuario
                HorasAcumuladasDTO horasAcumuladas = new HorasAcumuladasDTO(
                        asuetoTrabajadoDto,
                        cargaLaboralDiurnaDto,
                        extrasDiurnasDto,
                        extrasNocturnasDto
                );

                // Añadir el DTO a la lista
                horasAcumuladasList.add(horasAcumuladas);
            }

            // Devolver la lista de DTOs
            return ResponseEntity.ok(horasAcumuladasList);
        }
    }


