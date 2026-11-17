package com.laboratorio.exception;

public class TransaccionNoEncontradaException extends RuntimeException {

    public TransaccionNoEncontradaException(Long id) {
        super("No existe la transacción con id " + id);
    }
}
