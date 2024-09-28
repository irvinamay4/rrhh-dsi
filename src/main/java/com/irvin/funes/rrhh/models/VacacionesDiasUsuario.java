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
@Table(name = "vacaciones_dias_usuario")
public class VacacionesDiasUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fecha_inicio;
    private String fecha_fin;
    private double cantidad_dias;
    private String mes;
    private String año;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonIgnore // Esto evitará que el usuario completo aparezca en el JSON
    private Usuario usuario;
}
