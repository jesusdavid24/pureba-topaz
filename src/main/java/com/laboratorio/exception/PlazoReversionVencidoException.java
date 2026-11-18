package com.laboratorio.exception;

public class PlazoReversionVencidoException extends RuntimeException {

    public PlazoReversionVencidoException(Long id, long horasTranscurridas) {
        super("La transacción " + id + " no puede reversarse: han pasado " + horasTranscurridas
                + " horas desde su creación y el plazo máximo es de 24 horas");
    }
}
