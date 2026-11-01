package com.juniverse.records.practical;

import java.math.BigDecimal;
import java.util.List;

import java.util.logging.Logger;
import com.juniverse.records.AppLogger;
/**
 * Caso práctico: el estado de un pedido modelado como interfaz sellada de records. Los estados imposibles
 * (un pedido "enviado" sin número de guía) no se pueden representar, y cada transición se valida en tiempo de compilación.
 */
public final class OrdersDemo {
    private static final Logger LOGGER = AppLogger.get(OrdersDemo.class);


    private OrdersDemo() {
    }

    /**
     * Línea de pedido.
     *
     * @param product producto
     * @param quantity cantidad (mayor a cero)
     * @param unitPrice precio unitario
     */
    public record Line(String product, int quantity, BigDecimal unitPrice) {
        /** Valida la cantidad. */
        public Line {
            if (quantity <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser positiva");
            }
        }

        /**
         * Importe de la línea.
         *
         * @return cantidad por precio unitario
         */
        public BigDecimal subtotal() {
            return unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    /** Estados posibles de un pedido: cada uno lleva SOLO los datos que le corresponden. */
    public sealed interface Order permits Draft, Paid, Shipped, Cancelled {
    }

    /**
     * Borrador.
     *
     * @param lines líneas del pedido
     */
    public record Draft(List<Line> lines) implements Order {
        /** Copia defensiva. */
        public Draft {
            lines = List.copyOf(lines);
        }
    }

    /**
     * Pagado.
     *
     * @param lines líneas
     * @param total importe pagado
     */
    public record Paid(List<Line> lines, BigDecimal total) implements Order {
    }

    /**
     * Enviado: solo existe con una guía de rastreo.
     *
     * @param paid pedido ya pagado
     * @param trackingNumber número de guía
     */
    public record Shipped(Paid paid, String trackingNumber) implements Order {
    }

    /**
     * Cancelado.
     *
     * @param reason motivo
     */
    public record Cancelled(String reason) implements Order {
    }

    /**
     * Paga un borrador.
     *
     * @param draft borrador
     * @return pedido pagado
     */
    public static Paid pay(Draft draft) {
        BigDecimal total = draft.lines().stream().map(Line::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new Paid(draft.lines(), total);
    }

    /**
     * Describe el estado; el compilador exige cubrir los cuatro.
     *
     * @param order pedido
     * @return texto
     */
    public static String status(Order order) {
        return switch (order) {
            case Draft(List<Line> lines) -> "Borrador con " + lines.size() + " línea(s)";
            case Paid(List<Line> lines, BigDecimal total) -> "Pagado: $" + total;
            case Shipped(Paid paid, String tracking) -> "Enviado (guía " + tracking + "), total $" + paid.total();
            case Cancelled(String reason) -> "Cancelado: " + reason;
        };
    }

    /** Ejecuta la demostración. */
    public static void run() {
        Draft draft = new Draft(List.of(new Line("Teclado", 1, new BigDecimal("899.00")), new Line("Ratón", 2, new BigDecimal("349.50"))));
        Paid paid = pay(draft);
        for (Order o : new Order[]{draft, paid, new Shipped(paid, "MX-123456"), new Cancelled("sin stock")}) {
            LOGGER.info("  " + status(o));
        }
        try {
            new Line("Cable", 0, BigDecimal.ONE);
        } catch (IllegalArgumentException e) {
            LOGGER.info("Línea inválida rechazada: " + e.getMessage());
        }
    }
}
