package com.laboratorio.service;

import com.laboratorio.exception.EstadoTransaccionInvalidoException;
import com.laboratorio.exception.PlazoReversionVencidoException;
import com.laboratorio.exception.TransaccionNoEncontradaException;
import com.laboratorio.model.EstadoTransaccion;
import com.laboratorio.model.Transaccion;
import com.laboratorio.repository.TransaccionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransaccionServiceTest {

    private static final LocalDateTime AHORA = LocalDateTime.of(2026, 11, 17, 12, 0);

    @Mock
    private TransaccionRepository repository;

    private TransaccionService service;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(AHORA.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        service = new TransaccionService(repository, clock);
    }

    private Transaccion transaccion(EstadoTransaccion estado, LocalDateTime fechaCreacion) {
        return new Transaccion(new BigDecimal("100"), estado, fechaCreacion);
    }

    @Test
    void reversaDentroDeLas24HorasCambiaEstadoYPersiste() {
        Transaccion t = transaccion(EstadoTransaccion.APROBADA, AHORA.minusHours(23));
        when(repository.findById(1L)).thenReturn(Optional.of(t));

        service.reversar(1L);

        assertEquals(EstadoTransaccion.REVERSADA, t.getEstado());
        verify(repository).saveAndFlush(t);
    }

    @Test
    void reversaExactamenteA24HorasEsValida() {
        Transaccion t = transaccion(EstadoTransaccion.APROBADA, AHORA.minusHours(24));
        when(repository.findById(1L)).thenReturn(Optional.of(t));

        service.reversar(1L);

        assertEquals(EstadoTransaccion.REVERSADA, t.getEstado());
    }

    @Test
    void pasadasLas24HorasLanzaPlazoVencidoYNoPersiste() {
        Transaccion t = transaccion(EstadoTransaccion.APROBADA, AHORA.minusHours(24).minusSeconds(1));
        when(repository.findById(1L)).thenReturn(Optional.of(t));

        assertThrows(PlazoReversionVencidoException.class, () -> service.reversar(1L));

        assertEquals(EstadoTransaccion.APROBADA, t.getEstado());
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void estadoDistintoDeAprobadaLanzaEstadoInvalido() {
        Transaccion t = transaccion(EstadoTransaccion.PENDIENTE, AHORA.minusHours(1));
        when(repository.findById(1L)).thenReturn(Optional.of(t));

        assertThrows(EstadoTransaccionInvalidoException.class, () -> service.reversar(1L));

        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void transaccionInexistenteLanzaNoEncontrada() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TransaccionNoEncontradaException.class, () -> service.reversar(99L));
    }
}
