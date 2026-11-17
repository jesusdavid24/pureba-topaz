# Laboratorio: reversión de transacciones

API REST hecha con Spring Boot 3 (Java 17, Spring Data JPA, H2 en memoria) que permite **reversar una transacción** mediante un único endpoint.

```
PATCH /transacciones/{id}/reversar
```

## Reglas de negocio

| Situación | Respuesta |
|---|---|
| La transacción está `APROBADA` y fue creada hace 24 horas o menos | `204 No Content`; el estado pasa a `REVERSADA` y se persiste |
| El estado es distinto de `APROBADA` | `409 Conflict` con un mensaje que indica el estado actual |
| Han pasado más de 24 horas desde `fechaCreacion` | `422 Unprocessable Entity` |
| Otra solicitud modificó la transacción al mismo tiempo (`OptimisticLockException`) | `409 Conflict` indicando que fue modificada concurrentemente |
| La transacción no existe | `404 Not Found` |

Los errores se devuelven como JSON:

```json
{
  "status": 422,
  "error": "Unprocessable Entity",
  "mensaje": "La transacción 2 no puede reversarse: han pasado 30 horas desde su creación y el plazo máximo es de 24 horas",
  "timestamp": "2026-11-17T16:18:33.89063"
}
```

## Qué se hizo

- **Lógica de reversión** en `TransaccionService`: valida estado y plazo, cambia el estado a `REVERSADA` y guarda con `saveAndFlush`.
- **Concurrencia**: se usa el campo `@Version` de la entidad (bloqueo optimista). `saveAndFlush` hace que el conflicto de versión se detecte dentro del método.
- **Manejo de errores limpio**: excepciones de negocio propias (`EstadoTransaccionInvalidoException`, `PlazoReversionVencidoException`, `TransaccionNoEncontradaException`) traducidas a HTTP en un único `@RestControllerAdvice` (`GlobalExceptionHandler`). El controlador no tiene condicionales de códigos HTTP.
- **Reloj inyectable**: el servicio recibe un `Clock`, lo que permite fijar la hora en las pruebas de la regla de 24 horas.
- **Datos de ejemplo** (`DatosDePrueba`): se cargan al arrancar para poder probar el endpoint.

## Estructura

```
src/main/java/com/laboratorio/
├── config/        ClockConfig, DatosDePrueba
├── controller/    TransaccionController
├── exception/     excepciones de negocio, ErrorResponse, GlobalExceptionHandler
├── model/         Transaccion, EstadoTransaccion
├── repository/    TransaccionRepository
└── service/       TransaccionService
src/test/java/com/laboratorio/service/
└── TransaccionServiceTest
```

## Cómo ejecutarlo

Requisitos: Java 17 o superior y Maven.

```bash
mvn spring-boot:run
```

Datos de ejemplo cargados al arrancar:

| id | Estado | Antigüedad | Resultado esperado |
|---|---|---|---|
| 1 | APROBADA | 2 h | 204 |
| 2 | APROBADA | 30 h | 422 |
| 3 | PENDIENTE | 1 h | 409 |
| 4 | REVERSADA | 5 h | 409 |
| 5 | APROBADA | 3 h | 204 (útil para probar concurrencia) |

```bash
curl -i -X PATCH localhost:8080/transacciones/1/reversar
```

La base H2 vive en memoria, así que los datos se recrean en cada arranque. La consola está en `/h2-console` (solo para desarrollo).

## Pruebas

```bash
mvn test
```

`TransaccionServiceTest` usa Mockito para el repositorio y un `Clock` fijo. Cubre:

- reversión válida dentro de las 24 horas,
- el límite exacto de 24 horas (válido),
- más de 24 horas (422) sin persistir cambios,
- estado distinto de `APROBADA` (409),
- transacción inexistente (404).

> **Nota sobre las pruebas:** todos los tests fueron escritos con ayuda de Claude y revisados por mí.

## Notas

- En la carpeta docs estan las respuesta de la prueba tecnica.
