# Records y clases selladas (sealed) en Java 21

Ejemplo ejecutable de **modelado de datos inmutables** con `record` y de **jerarquías cerradas** con `sealed`, combinados con **pattern matching**. Java 21, sin dependencias externas.

## Idea clave

- Un **`record`** es una clase inmutable para datos: el compilador genera constructor, accesores, `equals`, `hashCode` y `toString`.
- Una clase o interfaz **`sealed`** declara con `permits` quién puede extenderla: la jerarquía queda **cerrada**.
- Un `switch` con **patrones** sobre una jerarquía sellada es **exhaustivo**: no necesita `default` y, si añades un tipo nuevo, el compilador te avisa de todos los lugares que debes actualizar.

## Qué contiene cada paquete

| Paquete | Clase | Contenido |
|---------|-------|-----------|
| `records` | `RecordsDemo` | Record básico, constructor compacto con validación y normalización, constructores y métodos adicionales, record genérico con copia defensiva, "modificar" creando otro (*wither*) y uso como clave de `Map` |
| `sealed` | `SealedDemo` | Clase sellada con `permits`, subclases `final` y `non-sealed`, y `switch` exhaustivo con guardas |
| `patternmatching` | `PatternMatchingDemo` | Árbol de expresiones con records, patrones de record (deconstrucción), patrones anidados, guardas `when`, `case null`, `instanceof` con patrón |
| `practical` | `OrdersDemo` | Estados de un pedido como interfaz sellada de records: los estados imposibles no se pueden representar |

## Cuándo usarlos

| Situación | Recomendación |
|-----------|---------------|
| DTO, valor de dominio o clave de mapa | `record` |
| Un valor que es "uno de varios casos" conocidos (estado, resultado, expresión) | `sealed interface` + `record`s |
| Necesitas herencia abierta o estado mutable | Clase normal |
| Entidad JPA | Clase normal (los records no sirven como `@Entity`) |

## Ojo con

- Un record es inmutable **solo de forma superficial**: si contiene una lista, haz una copia defensiva (`List.copyOf`).
- Los accesores se llaman `name()`, no `getName()`.
- Los records no pueden extender otras clases (sí pueden implementar interfaces).

## Requisitos

- JDK 21 y Maven 3.9 o superior.

## Compilar y ejecutar

```bash
mvn compile exec:java

# o como JAR ejecutable (fat jar)
mvn clean package
java -jar target/mvn-records-sealed-1.0.0-SNAPSHOT-launcher.jar
```

## Licencia

Código de ejemplo de uso libre con fines educativos.
