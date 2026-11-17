import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {

        List<Transaccion> lista = List.of(
            new Transaccion("A", "CREDITO", new BigDecimal("1000"), LocalDate.now()),
            new Transaccion("A", "DEBITO", new BigDecimal("300"), LocalDate.now()),
            new Transaccion("B", "CREDITO", new BigDecimal("500"), LocalDate.now()),
            new Transaccion("A", "DEBITO", new BigDecimal("-50"), LocalDate.now())
        );

        System.out.println(saldoPorCuenta(lista));

    }



    public static Map<String, BigDecimal> saldoPorCuenta(List<Transaccion> transacciones) {

        if (transacciones == null || transacciones.isEmpty() ) {
            return new TreeMap<>();
        }

        return transacciones.stream().filter(t -> t.monto() != null && t.monto().signum() >= 0)
                .collect(Collectors.groupingBy(
                        Transaccion::cuenta,
                        TreeMap::new,
                        Collectors.mapping(
                                t -> "CREDITO".equals(t.tipo()) ? t.monto() : t.monto().negate(),
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ));
    }

}
