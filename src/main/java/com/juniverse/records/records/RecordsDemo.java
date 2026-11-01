package com.juniverse.records.records;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import java.util.logging.Logger;
import com.juniverse.records.AppLogger;
/**
 * Records (Java 16): clases inmutables para modelar datos, sin código repetitivo.
 *
 * <p>Un {@code record} declara sus componentes y el compilador genera el constructor, los accesores ({@code name()}, no
 * {@code getName()}), {@code equals}, {@code hashCode} y {@code toString}. Los campos son {@code final}: el objeto es inmutable.</p>
 */
public final class RecordsDemo {
    private static final Logger LOGGER = AppLogger.get(RecordsDemo.class);


    private RecordsDemo() {
    }

    /**
     * Record sencillo.
     *
     * @param x coordenada horizontal
     * @param y coordenada vertical
     */
    public record Point(int x, int y) {
    }

    /**
     * Record con constructor compacto: valida y normaliza los datos antes de asignarlos.
     *
     * @param name nombre (no vacío, sin espacios sobrantes)
     * @param email correo (se guarda en minúsculas)
     * @param birthDate fecha de nacimiento (no puede ser futura)
     */
    public record Person(String name, String email, LocalDate birthDate) {
        /** Constructor compacto: se ejecuta antes de asignar los campos. */
        public Person {
            Objects.requireNonNull(name, "name");
            name = name.strip();
            if (name.isEmpty()) {
                throw new IllegalArgumentException("El nombre no puede estar vacío");
            }
            email = email.toLowerCase();
            if (birthDate.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura");
            }
        }

        /**
         * Constructor adicional con un valor por defecto.
         *
         * @param name nombre
         * @param email correo
         */
        public Person(String name, String email) {
            this(name, email, LocalDate.of(2000, 1, 1));
        }

        /**
         * Método de instancia: un record puede tener comportamiento.
         *
         * @return edad en años
         */
        public int age() {
            return LocalDate.now().getYear() - birthDate.getYear();
        }

        /**
         * Método de fábrica estático.
         *
         * @param fullLine texto con el formato "nombre;correo"
         * @return la persona
         */
        public static Person parse(String fullLine) {
            String[] parts = fullLine.split(";");
            return new Person(parts[0], parts[1]);
        }
    }

    /**
     * Record genérico con una lista: como el record solo es inmutable "de forma superficial", se hace una copia defensiva.
     *
     * @param title título
     * @param tags etiquetas
     * @param <T> tipo de las etiquetas
     */
    public record Tagged<T>(String title, List<T> tags) {
        /** Copia defensiva e inmodificable de la lista recibida. */
        public Tagged {
            tags = List.copyOf(tags);
        }
    }

    /**
     * "Modificar" un record = crear otro nuevo con los cambios (patrón "wither").
     *
     * @param p punto original
     * @param dx desplazamiento horizontal
     * @return nuevo punto
     */
    public static Point moved(Point p, int dx) {
        return new Point(p.x() + dx, p.y());
    }

    /** Ejecuta la demostración. */
    public static void run() {
        Point a = new Point(2, 3);
        Point b = new Point(2, 3);
        LOGGER.info("toString: " + a + " | equals por valor: " + a.equals(b) + " | mismo hashCode: " + (a.hashCode() == b.hashCode()));
        LOGGER.info("Inmutable: moved(a, 5) = " + moved(a, 5) + ", y a sigue siendo " + a);

        Person p = new Person("  Ana López ", "ANA@Mail.COM");
        LOGGER.info("Constructor compacto normaliza: " + p.name() + " / " + p.email() + " / edad ~" + p.age());
        LOGGER.info("Método de fábrica: " + Person.parse("Luis;LUIS@mail.com"));
        try {
            new Person("   ", "x@y.z");
        } catch (IllegalArgumentException e) {
            LOGGER.info("Validación: " + e.getMessage());
        }

        List<String> original = new ArrayList<>(List.of("java", "21"));
        Tagged<String> t = new Tagged<>("Curso", original);
        original.add("hackeado");
        LOGGER.info("Copia defensiva -> tags: " + t.tags() + " (la lista original cambió pero el record no)");

        // Los records como claves de mapa funcionan gracias a equals/hashCode generados
        Map<Point, String> map = Map.of(new Point(0, 0), "origen", new Point(1, 1), "diagonal");
        LOGGER.info("Record como clave de Map: " + map.get(new Point(1, 1)));
    }
}
