package com.laboratorio.service;

import com.laboratorio.exception.EstadoTransaccionInvalidoException;
import com.laboratorio.exception.PlazoReversionVencidoException;
import com.laboratorio.exception.TransaccionNoEncontradaException;
import com.laboratorio.model.EstadoTransaccion;
import com.laboratorio.model.Transaccion;
import com.laboratorio.repository.TransaccionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class TransaccionService {

    static final Duration PLAZO_REVERSION = Duration.ofHours(24);

    private final TransaccionRepository transaccionRepository;
    private final Clock clock;

    public TransaccionService(TransaccionRepository transaccionRepository, Clock clock) {
        this.transaccionRepository = transaccionRepository;
        this.clock = clock;
    }

    @Transactional
    public void reversar(Long id) {
        Transaccion transaccion = transaccionRepository.findById(id)
                .orElseThrow(() -> new TransaccionNoEncontradaException(id));

        if (transaccion.getEstado() != EstadoTransaccion.APROBADA) {
            throw new EstadoTransaccionInvalidoException(id, transaccion.getEstado());
        }

        Duration transcurrido = Duration.between(transaccion.getFechaCreacion(), LocalDateTime.now(clock));
        if (transcurrido.compareTo(PLAZO_REVERSION) > 0) {
            throw new PlazoReversionVencidoException(id, transcurrido.toHours());
        }

        transaccion.setEstado(EstadoTransaccion.REVERSADA);
        transaccionRepository.saveAndFlush(transaccion);
    }
}
