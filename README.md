# Juego de la Vida - TPE Final Libre - Programacion 2 TUDAI - Tandil
## Descripción del Proyecto
Este proyecto se crea a partir de la consigna del TPE Libre - Juego de la vida (2026).pdf.

## Índice
1. [Estructura del Proyecto](#estructura-del-proyecto)
2. [Compilación](#compilación)
3. [Ejecución](#ejecución)
4. [Arquitectura y Diseño](#arquitectura-y-diseño)
5. [Principios SOLID](#principios-solid-aplicados)
6. [Cómo Extender el Proyecto](#cómo-extender-el-proyecto)

---

## Estructura del Proyecto

```
JuegoDeLaVida/
├── src/
│   ├── module-info.java.bak      # Backup de la configuración del módulo
│   ├── juego/
│   │   └── JuegoDeLaVida.java    # Punto de entrada de la aplicación
│   ├── modelo/
│   │   ├── Tablero.java          # Lógica del tablero de juego
│   │   ├── Celda.java            # Representación de una celda
│   │   ├── Estado.java           # Interfaz para patrones de estado
│   │   ├── EstadoVivo.java       # Implementación: celda viva
│   │   ├── EstadoMuerto.java     # Implementación: celda muerta
│   │   ├── EstadoEnfermo.java    # Implementación: celda enferma
│   │   └── EstadoLatente.java    # Implementación: celda latente
│   ├── vista/
│   │   └── VistaJuego.java       # Interfaz gráfica Swing del usuario
│   └── io/
│       └── CargadorTablero.java  # Carga estados desde archivo
├── bin/                          # Archivos compilados (.class)
└── ejemplos/                     # Archivos de configuración de ejemplo
    ├── ejemplo1.txt
    ├── ejemplo2.txt
    ├── ejemplo3.txt
    └── ejemplo4.txt

```

---

## Compilación

### Desde Línea de Comandos

```bash
cd JuegoDeLaVida
javac -d bin src/modelo/*.java src/juego/*.java src/vista/*.java src/io/*.java
```

## Ejecución

### Desde Línea de Comandos

1. Una vez ya posicionado dentro de la carpeta JuegoDeLaVida

```bash
java -cp bin juego.JuegoDeLaVida
```

### Desde IDE

1. Abre el archivo `JuegoDeLaVida.java`
2. Ejecuta el método `main()` (botón de play o tecla F5)

### Interfaz de Ejecución Actual

La versión presente en el proyecto usa una interfaz Swing. Los controles disponibles son:

- **Cargar desde archivo**: abre un `JFileChooser` para elegir un tablero desde `ejemplos/` o cualquier archivo `.txt`.
- **Generar aleatorio**: crea un tablero con tamaños definidos por los campos `Filas` y `Columnas`.
- **Iniciar simulación**: arranca el bucle con `Timer` y el `delay` configurado en milisegundos.
- **Siguiente paso**: avanza una única generación manualmente.
- **Pausar / Reanudar**: controla la ejecución automática.

### Formato de Archivo Soportado

El archivo debe tener el siguiente formato:

```
<filas> <columnas>
O.X.O
.O...
X...O
O.E.O
...O.
```

Caracteres válidos:
- `O` - celda viva
- `.` - celda muerta
- `E` - celda enferma
- `X` - celda latente

- **Nota:** El programa es insensible a mayúsculas/minúsculas al leer archivos, y cualquier carácter no reconocido será tratado automáticamente como una celda muerta `.`. También, debe tomarse en cuenta que al crear un archivo, la posición de dónde se declaran las filas y las columnas es absoluta (siempre el primer renglón, el primer caracter debe ser valido Integer y luego debe estar separado por un espacio para el siguiente valor Integer, pues uso Scanner.hasNestInt()).

### Controles Durante la Simulación

Los campos de configuración visibles en la interfaz son:

- `Filas`
- `Columnas`
- `Generaciones`
- `Delay (ms)`

Comportamiento actual:
- `0 generaciones` = la simulación se ejecuta hasta que el tablero se estabiliza o no hay cambios.
- `Delay` = velocidad de la animación en milisegundos.

---

## Arquitectura y Diseño

### Separación Modelo-Vista

El proyecto implementa una arquitectura limpia que separa la lógica del juego de su presentación gráfica:

```
┌─────────────────────────────────────────────────┐
│               VISTA                            │
│              VistaJuego.java                   │
│  - Interfaz gráfica con JFrame                 │
│  - Botones de carga, aleatoriedad y control    │
│  - Render del tablero y manejo de Timer        │
└────────────────┬────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────┐
│                   IO                           │
│         CargadorTablero.java                   │
│  - Parseo de archivos                          │
│  - Manejo de I/O                               │
└────────────────┬────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────┐
│           MODELO                               │
│      Tablero, Celda, Estado                    │
│  - Algoritmo del juego                         │
│  - Cálculo de generaciones                     │
│  - Reglas y transiciones de estado             │
└─────────────────────────────────────────────────┘
```

### Clases Principales y Responsabilidades

#### 1. **Tablero.java** - Orquestación del Juego
- **Responsabilidad**: Gestionar el estado global del tablero y la evolución de generaciones.
- **Métodos clave**:
  - `avanzarGeneracion()`: Calcula la siguiente generación y devuelve si hubo cambios.
  - `contarVecinosVivos(int fila, int col)`: Cuenta celdas vivas adyacentes.
  - `mostrar()`: Imprime el tablero en consola.
  - `setCelda()` / `getCelda()`: Acceso a celdas específicas.

**Lógica de evolución**:
```java
// Para cada celda:
// 1. Contar vecinos vivos
// 2. Calcular siguiente estado (delegado a Estado)
// 3. Guardar el estado previsto
// 4. Actualizar el estado al final de la iteración
// 5. Reportar si hubo cambios
```

#### 2. **Celda.java** - Unidad Atómica
- **Responsabilidad**: Representar una celda individual y su evolución.
- **Métodos clave**:
  - `calcularSig(int vecinosVivos)`: Delega a `Estado` para calcular el siguiente estado.
  - `evolucionar()`: Transiciona al siguiente estado.
  - `isViva()`: Consulta si la celda está viva.

**Patrón**: Actúa como contexto en el patrón State.

#### 3. **Estado.java (Interfaz)** - Patrón Strategy
- **Responsabilidad**: Definir el contrato para comportamientos de estado.
- **Métodos**:
  - `boolean isViva()`: ¿Es una celda viva?
  - `char getRepresentacion()`: Símbolo visual
  - `Estado SigEstado(int vecinosVivos)`: Calcula transición

**Ventaja**: Nueva lógica sin modificar `Celda` o `Tablero`.

#### 4. **Implementaciones de Estado**

**EstadoVivo.java**
- Muere si tiene < 2 o > 3 vecinos vivos.
- Con 2 o 3 vecinos vive y puede enfermarse con probabilidad 25%.
- Representación: `O`

**EstadoMuerto.java**
- Se vuelve viva con exactamente 3 vecinos vivos.
- Permanece muerta en cualquier otro caso.
- Representación: `.`

**EstadoEnfermo.java**
- Se considera viva.
- En la siguiente generación muere automáticamente.
- Representación: `E`

**EstadoLatente.java**
- Se considera muerta.
- Despierta con exactamente 1 vecino vivo.
- Representación: `X`

#### 5. **VistaJuego.java** - Interfaz de Usuario
- **Responsabilidad**: Orquestar la interacción gráfica con Swing.
- **Métodos clave**:
  - `iniciar()`: Hace visible la ventana principal.
  - `cargarDesdeArchivo()`: Abre un selector de archivo.
  - `configurarManual()`: Genera un tablero aleatorio.
  - `iniciarBucle()`: Ejecuta la simulación con `Timer`.
  - `avanzarUnaGeneracion()`: Avanza una generación manual.

**Flujo actual**:
```
1. Mostrar ventana Swing
2. Usuario elige cargar archivo o generar aleatorio
3. Se crea el Tablero
4. Se renderiza el estado visual
5. Se avanza por pasos o con Timer
```

#### 6. **CargadorTablero.java** - Entrada/Salida
- **Responsabilidad**: Parsear archivos de configuración.
- **Métodos clave**:
  - `cargarDesdeArchivo(String ruta)`: Carga un tablero desde archivo.
  - `crearEstadoSegunCaracter(char c)`: Mapeo de caracteres a estados.

**Validaciones**:
- Verifica filas y columnas.
- Completa líneas cortas con celdas muertas.
- Lanza excepciones descriptivas.

---

## Principios SOLID Aplicados

### 1. **Single Responsibility Principle (SRP)**
Cada clase tiene una única responsabilidad bien definida:
- `Tablero` → Gestión del tablero y evolución
- `Celda` → Representación de celda y delegación
- `Estado` → Comportamiento específico del estado
- `VistaJuego` → Presentación e interacción
- `CargadorTablero` → Entrada/Salida

**Beneficio**: Cambios en un aspecto no afectan otros

### 2. **Open/Closed Principle (OCP)**
El sistema es abierto para extensión pero cerrado para modificación:
- Nuevos estados pueden crearse implementando la interfaz `Estado`
- Sin cambiar código existente en `Tablero` o `Celda`
- El cargador se actualiza de forma aislada

**Ejemplo**: Agregar `EstadoRoboto` no requiere recompilar lógica

### 3. **Liskov Substitution Principle (LSP)**
Todas las implementaciones de `Estado` son intercambiables:
```java
Estado estado1 = new EstadoVivo();
Estado estado2 = new EstadoEnfermo();
Estado estado3 = new EstadoLatente();
// Todas pueden usarse en Celda sin problemas
```

**Garantía**: Contrato consistente

### 4. **Interface Segregation Principle (ISP)**
La interfaz `Estado` define solo lo necesario:
```java
public interface Estado {
    boolean isViva();
    char getRepresentacion();
    Estado SigEstado(int vecinosVivos);
}
```

**No incluye métodos innecesarios**: Cada clase implementa lo que necesita

### 5. **Dependency Inversion Principle (DIP)**
- `Tablero` depende de la abstracción `Estado`, no de implementaciones
- `Celda` recibe `Estado` en su constructor (inyección)
- La lógica está desacoplada
- El Modelo es independiente de la configuración inicial.

```java
private Estado estadoActual;  // Depende de abstracción
```

---

## Cómo Extender el Proyecto

### Escenario 1: Agregar un Nuevo Estado

Supongamos que se quiere agregar un estado `EstadoRoboto`:
- Es considerado "vivo"
- Se representa con `R`
- Muere si tiene más de 4 vecinos vivos.

#### Paso 1: Crear la Nueva Clase de Estado

Crea `EstadoRoboto.java` en `src/modelo/`:

```java
package modelo;

public class EstadoRoboto implements Estado {
    private final int LIMITE_VECINOS = 4;

    @Override
    public boolean isViva() {
        return true;  // Es considerado vivo
    }

    @Override
    public char getRepresentacion() {
        return 'R';  // Símbolo visual
    }

    @Override
    public Estado SigEstado(int vecinosVivos) {
        if (vecinosVivos > LIMITE_VECINOS) {
            return new EstadoMuerto();  // Muere por sobrepoblación
        }
        return this;  // Sobrevive
    }
}
```

#### Paso 2: Actualizar el Cargador

Modifica `CargadorTablero.java` en el método `crearEstadoSegunCaracter()`:

```java
private static Estado crearEstadoSegunCaracter(char c) {
    return switch (Character.toUpperCase(c)) {
        case 'O' -> new EstadoVivo();
        case 'E' -> new EstadoEnfermo();
        case 'X' -> new EstadoLatente();
        case 'R' -> new EstadoRoboto();    // ← NUEVA LÍNEA
        case '.' -> new EstadoMuerto();
        default  -> new EstadoMuerto();
    };
}
```
#### Paso 3: Actualizar la Vista

Modifica `VistaJuego.java` en el método `colorPorEstado()`:

```java
private Color colorPorEstado(char estado) {
    switch (estado) {
        case 'O':
            return new Color(0, 128, 0);
        case 'E':
            return new Color(255, 215, 0);
        case 'X':
            return new Color(173, 216, 230);
        case 'R':
            return new Color(255, 0, 0);       // ← NUEVA LÍNEA
        default:
            return Color(0, 0, 0);
    }
}

```

---

## Notas Técnicas

### Patrones de Diseño Utilizados
- **State Pattern** (Patrón Estado): `Celda` + `Estado` para transiciones
- **Template Method**: Flujo de ejecución en `VistaJuego`
- **Factory Pattern**: Creación de estados en `CargadorTablero`

---

**Última actualización**: Julio 2026

