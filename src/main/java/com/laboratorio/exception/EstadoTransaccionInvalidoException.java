package com.laboratorio.exception;

import com.laboratorio.model.EstadoTransaccion;

public class EstadoTransaccionInvalidoException extends RuntimeException {

    public EstadoTransaccionInvalidoException(Long id, EstadoTransaccion estadoActual) {
        super("La transacción " + id + " no puede reversarse: su estado es " + estadoActual
                + " y solo se pueden reversar transacciones APROBADAS");
    }
}
