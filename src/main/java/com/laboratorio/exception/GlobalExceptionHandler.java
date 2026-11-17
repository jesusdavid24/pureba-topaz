package com.laboratorio.exception;

import jakarta.persistence.OptimisticLockException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TransaccionNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> noEncontrada(TransaccionNoEncontradaException ex) {
        return construir(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(EstadoTransaccionInvalidoException.class)
    public ResponseEntity<ErrorResponse> estadoInvalido(EstadoTransaccionInvalidoException ex) {
        return construir(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(PlazoReversionVencidoException.class)
    public ResponseEntity<ErrorResponse> plazoVencido(PlazoReversionVencidoException ex) {
        return construir(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    @ExceptionHandler({OptimisticLockException.class, OptimisticLockingFailureException.class})
    public ResponseEntity<ErrorResponse> bloqueoOptimista(Exception ex) {
        return construir(HttpStatus.CONFLICT,
                "La transacción fue modificada concurrentemente. Vuelve a consultarla e intenta de nuevo");
    }

    private ResponseEntity<ErrorResponse> construir(HttpStatus status, String mensaje) {
        ErrorResponse body = new ErrorResponse(status.value(), status.getReasonPhrase(), mensaje, LocalDateTime.now());
        return ResponseEntity.status(status).body(body);
    }
}
