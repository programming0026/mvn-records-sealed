package com.juniverse.records.sealed;

/**
 * Clases e interfaces selladas (Java 17): una jerarquía <b>cerrada</b>. La cláusula {@code permits} indica exactamente
 * qué clases pueden extenderla, así el compilador conoce todos los casos posibles.
 *
 * <p>Cada subclase debe ser {@code final}, {@code sealed} (con sus propios permitidos) o {@code non-sealed} (abierta de nuevo).</p>
 */
public final class SealedDemo {

    private SealedDemo() {
    }

    /** Jerarquía sellada: solo estas tres clases pueden ser un {@code Vehicle}. */
    public abstract static sealed class Vehicle permits Car, Truck, Motorcycle {
        private final String plate;

        /**
         * Crea el vehículo.
         *
         * @param plate matrícula
         */
        protected Vehicle(String plate) {
            this.plate = plate;
        }

        /**
         * Matrícula.
         *
         * @return placa
         */
        public String plate() {
            return plate;
        }
    }

    /** Subclase final: no se puede extender más. */
    public static final class Car extends Vehicle {
        private final int doors;

        /**
         * Crea el auto.
         *
         * @param plate matrícula
         * @param doors número de puertas
         */
        public Car(String plate, int doors) {
            super(plate);
            this.doors = doors;
        }

        /**
         * Puertas.
         *
         * @return número de puertas
         */
        public int doors() {
            return doors;
        }
    }

    /** Subclase final. */
    public static final class Truck extends Vehicle {
        private final double capacityTons;

        /**
         * Crea el camión.
         *
         * @param plate matrícula
         * @param capacityTons capacidad en toneladas
         */
        public Truck(String plate, double capacityTons) {
            super(plate);
            this.capacityTons = capacityTons;
        }

        /**
         * Capacidad.
         *
         * @return toneladas
         */
        public double capacityTons() {
            return capacityTons;
        }
    }

    /** Subclase {@code non-sealed}: vuelve a ser extensible (cualquiera puede heredar de ella). */
    public non-sealed static class Motorcycle extends Vehicle {
        /**
         * Crea la moto.
         *
         * @param plate matrícula
         */
        public Motorcycle(String plate) {
            super(plate);
        }
    }

    /** Extensión permitida porque {@code Motorcycle} es non-sealed. */
    public static class ElectricMotorcycle extends Motorcycle {
        /**
         * Crea la moto eléctrica.
         *
         * @param plate matrícula
         */
        public ElectricMotorcycle(String plate) {
            super(plate);
        }
    }

    /**
     * Cuota de peaje. El {@code switch} sobre una jerarquía sellada es EXHAUSTIVO: no necesita {@code default}, y si se agrega
     * un nuevo tipo permitido el compilador marca este método como incompleto.
     *
     * @param vehicle vehículo
     * @return importe del peaje
     */
    public static double toll(Vehicle vehicle) {
        return switch (vehicle) {
            case Car c when c.doors() > 4 -> 120.0;
            case Car c -> 80.0;
            case Truck t -> 150.0 + t.capacityTons() * 10;
            case Motorcycle m -> 40.0;
        };
    }

    /** Ejecuta la demostración. */
    public static void run() {
        Vehicle[] flota = {new Car("ABC-123", 4), new Car("VAN-777", 5), new Truck("TRK-001", 12.5),
            new Motorcycle("MOT-9"), new ElectricMotorcycle("ELE-1")};
        for (Vehicle v : flota) {
            System.out.printf("  %-8s (%s) -> peaje $%.2f%n", v.plate(), v.getClass().getSimpleName(), toll(v));
        }
        System.out.println("Clase sellada: " + Vehicle.class.isSealed() + ", permitidas: "
                + java.util.Arrays.stream(Vehicle.class.getPermittedSubclasses()).map(Class::getSimpleName).toList());
    }
}
