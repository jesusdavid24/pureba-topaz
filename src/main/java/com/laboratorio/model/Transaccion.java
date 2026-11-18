package com.laboratorio.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    private EstadoTransaccion estado;

    private LocalDateTime fechaCreacion;

    @Version
    private Long version;

    protected Transaccion() {

    }

    public Transaccion(BigDecimal monto, EstadoTransaccion estado, LocalDateTime fechaCreacion) {
        this.monto = monto;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
    }
}
