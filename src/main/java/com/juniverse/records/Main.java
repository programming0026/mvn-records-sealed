package com.juniverse.records;

import com.juniverse.records.patternmatching.PatternMatchingDemo;
import com.juniverse.records.practical.OrdersDemo;
import com.juniverse.records.records.RecordsDemo;
import com.juniverse.records.sealed.SealedDemo;

/**
 * Records y clases selladas de Java 21: modelado de datos inmutables y jerarquías cerradas con pattern matching.
 */
public final class Main {

    private Main() {
    }

    private static void title(String text) {
        System.out.println("\n=========== " + text + " ===========");
    }

    /**
     * Punto de entrada.
     *
     * @param args no se usan
     */
    public static void main(String[] args) {
        // Salida en UTF-8 para que se vean bien los acentos en cualquier consola
        System.setOut(new java.io.PrintStream(System.out, true, java.nio.charset.StandardCharsets.UTF_8));
        title("1. RECORDS: datos inmutables");
        RecordsDemo.run();
        title("2. CLASES SELLADAS: jerarquías cerradas");
        SealedDemo.run();
        title("3. PATTERN MATCHING con records y sealed");
        PatternMatchingDemo.run();
        title("4. CASO PRÁCTICO: estados de un pedido");
        OrdersDemo.run();
    }
}
