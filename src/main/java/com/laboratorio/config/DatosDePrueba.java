package com.laboratorio.config;

import com.laboratorio.model.EstadoTransaccion;
import com.laboratorio.model.Transaccion;
import com.laboratorio.repository.TransaccionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Datos de ejemplo para probar el endpoint (H2 en memoria: se recrean en cada arranque). */
@Configuration
public class DatosDePrueba {

    @Bean
    CommandLineRunner cargarDatos(TransaccionRepository repository) {
        return args -> {
            LocalDateTime ahora = LocalDateTime.now();
            repository.saveAll(List.of(
                    // id 1: APROBADA y reciente -> reversión válida (204)
                    new Transaccion(new BigDecimal("1500.00"), EstadoTransaccion.APROBADA, ahora.minusHours(2)),
                    // id 2: APROBADA pero con más de 24 h -> 422
                    new Transaccion(new BigDecimal("800.00"), EstadoTransaccion.APROBADA, ahora.minusHours(30)),
                    // id 3: PENDIENTE -> 409
                    new Transaccion(new BigDecimal("250.00"), EstadoTransaccion.PENDIENTE, ahora.minusHours(1)),
                    // id 4: ya REVERSADA -> 409
                    new Transaccion(new BigDecimal("990.00"), EstadoTransaccion.REVERSADA, ahora.minusHours(5)),
                    // id 5: APROBADA y reciente -> para probar concurrencia
                    new Transaccion(new BigDecimal("4200.00"), EstadoTransaccion.APROBADA, ahora.minusHours(3))
            ));
        };
    }
}
