package com.laboratorio.exception;

import com.laboratorio.model.EstadoTransaccion;

/** La transacción no está en un estado que permita la reversión (409). */
public class EstadoTransaccionInvalidoException extends RuntimeException {

    public EstadoTransaccionInvalidoException(Long id, EstadoTransaccion estadoActual) {
        super("La transacción " + id + " no puede reversarse: su estado es " + estadoActual
                + " y solo se pueden reversar transacciones APROBADAS");
    }
}
