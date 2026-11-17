import java.math.BigDecimal;
import java.time.LocalDate;

public record Transaccion(String cuenta, String tipo, BigDecimal monto, LocalDate fecha) {
    // You can add methods or additional logic here if needed
}