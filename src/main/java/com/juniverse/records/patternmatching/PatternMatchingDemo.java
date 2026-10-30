package com.juniverse.records.patternmatching;

import java.util.List;

/**
 * Modelado de datos con records + interfaces selladas + pattern matching (Java 21): el estilo de "tipos algebraicos".
 *
 * <p>Se modela un árbol de expresiones matemáticas: cada nodo es un record inmutable y la jerarquía es cerrada.
 * Las operaciones se escriben como {@code switch} con patrones de record (deconstrucción), exhaustivos y sin casts.</p>
 */
public final class PatternMatchingDemo {

    private PatternMatchingDemo() {
    }

    /** Expresión: solo puede ser una de estas variantes. */
    public sealed interface Expr permits Num, Add, Mul, Neg {
    }

    /**
     * Número.
     *
     * @param value valor
     */
    public record Num(double value) implements Expr {
    }

    /**
     * Suma.
     *
     * @param left operando izquierdo
     * @param right operando derecho
     */
    public record Add(Expr left, Expr right) implements Expr {
    }

    /**
     * Multiplicación.
     *
     * @param left operando izquierdo
     * @param right operando derecho
     */
    public record Mul(Expr left, Expr right) implements Expr {
    }

    /**
     * Negación.
     *
     * @param inner expresión negada
     */
    public record Neg(Expr inner) implements Expr {
    }

    /**
     * Evalúa la expresión. Los patrones de record deconstruyen cada nodo y, al ser la jerarquía sellada, no hace falta {@code default}.
     *
     * @param e expresión
     * @return valor numérico
     */
    public static double eval(Expr e) {
        return switch (e) {
            case Num(double v) -> v;
            case Add(Expr l, Expr r) -> eval(l) + eval(r);
            case Mul(Expr l, Expr r) -> eval(l) * eval(r);
            case Neg(Expr inner) -> -eval(inner);
        };
    }

    /**
     * Convierte la expresión en texto.
     *
     * @param e expresión
     * @return representación legible
     */
    public static String show(Expr e) {
        return switch (e) {
            case Num(double v) -> v == Math.floor(v) ? String.valueOf((long) v) : String.valueOf(v);
            case Add(Expr l, Expr r) -> "(" + show(l) + " + " + show(r) + ")";
            case Mul(Expr l, Expr r) -> show(l) + " * " + show(r);
            case Neg(Expr inner) -> "-" + show(inner);
        };
    }

    /**
     * Simplifica la expresión con patrones anidados y guardas ({@code when}).
     *
     * @param e expresión
     * @return expresión equivalente más simple
     */
    public static Expr simplify(Expr e) {
        return switch (e) {
            case Add(Num(double a), Num(double b)) -> new Num(a + b);
            case Add(Expr l, Num(double z)) when z == 0 -> simplify(l);
            case Add(Num(double z), Expr r) when z == 0 -> simplify(r);
            case Mul(Expr l, Num(double o)) when o == 1 -> simplify(l);
            case Mul(Num(double o), Expr r) when o == 1 -> simplify(r);
            case Mul(Expr l, Num(double z)) when z == 0 -> new Num(0);
            case Neg(Neg(Expr inner)) -> simplify(inner);
            case Add(Expr l, Expr r) -> new Add(simplify(l), simplify(r));
            case Mul(Expr l, Expr r) -> new Mul(simplify(l), simplify(r));
            case Neg(Expr inner) -> new Neg(simplify(inner));
            case Num n -> n;
        };
    }

    /**
     * Pattern matching sobre tipos y valores cualesquiera: patrones de tipo, guardas y {@code null}.
     *
     * @param o objeto de cualquier tipo
     * @return descripción
     */
    public static String describe(Object o) {
        return switch (o) {
            case null -> "es null";
            case Integer i when i < 0 -> "entero negativo " + i;
            case Integer i -> "entero " + i;
            case String s when s.isBlank() -> "texto vacío";
            case String s -> "texto de " + s.length() + " caracteres";
            case List<?> list when list.isEmpty() -> "lista vacía";
            case List<?> list -> "lista de " + list.size() + " elementos";
            case Expr ex -> "expresión que vale " + eval(ex);
            default -> "otro tipo: " + o.getClass().getSimpleName();
        };
    }

    /** Ejecuta la demostración. */
    public static void run() {
        Expr e = new Add(new Mul(new Num(3), new Num(1)), new Add(new Num(4), new Num(0)));
        System.out.println("Expresión:  " + show(e));
        System.out.println("Evaluación: " + eval(e));
        Expr simple = simplify(e);
        System.out.println("Simplificada: " + show(simple) + "  (" + simple + ")");
        System.out.println("Doble negación: " + show(simplify(new Neg(new Neg(new Num(7))))));

        for (Object o : new Object[]{null, -4, 10, "  ", "hola", List.of(), List.of(1, 2), new Neg(new Num(5)), 3.14}) {
            System.out.println("  describe -> " + describe(o));
        }

        // instanceof con patrón (Java 16): comprueba el tipo y declara la variable en un solo paso
        Object dato = "Java 21";
        if (dato instanceof String s && s.length() > 5) {
            System.out.println("instanceof con patrón: '" + s + "' tiene más de 5 caracteres");
        }
        if (e instanceof Add(Expr izq, Expr der)) {
            System.out.println("instanceof con patrón de record: raíz Add, izquierda = " + show(izq));
        }
    }
}
