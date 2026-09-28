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

### Interfaz
Se usa una interfaz Swing. Los controles/botones disponibles son:
- **Cargar desde archivo**: abre un `JFileChooser` para elegir un tablero desde `ejemplos/` o cualquier archivo `.txt`.
- **Generar tablero aleatorio**: crea un tablero con tamaños definidos por los campos `Filas` y `Columnas`.
  - Para generar un archivo aleatorio, se debe tomar en cuenta los valores presentes en los campos Filas, Columnas, Generaciones y Delay(ms). Cada uno de estos campos vienen con valores de default. Si se presiona el botón Generar tablero aleatorio sin modificar niguno, entonces se generará un tablero de 10 filas x 10 columnas, donde cada celda puede tomar cualquiera de los valores habilitados con un 25% de probabilidad y correrá hasta que el tablero se estabilice sin importar cuantos ciclos generacionales deberá pasar (Generaciones dice 0, eso significa que no se ingresa cuantas generaciones se recorrerán), a una velocidad de 500 milisegundos.
- Para responder a la consigna `4. Opcional (bonus): GUI usando Swing/JavaFX con visualización en tiempo real y controles (start/stop/step/speed).` (Iniciar/Pausar/Siguiente/Delay (ms){campo milisegundos})
    - **Iniciar**: arranca el bucle con `Timer` y el `Delay` configurado en milisegundos. Interpreto speed como la posibilidad de decidir la velocidad que toma cada ciclo generacional.
    - **Pausar / Reanudar**: controla la ejecución automática. Cambia el nombre del botón, si se Pausa cambia a Reanudar y vice versa. 
    - **Siguiente**: avanza una única generación manualmente.

### Formato de Archivo
El archivo debe tener el siguiente formato:
- Debe ser de tipo .txt
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

- **Nota:** El programa es insensible a mayúsculas/minúsculas al leer archivos, y cualquier carácter no reconocido será tratado automáticamente como una celda muerta `.`. También, debe tomarse en cuenta que al crear un archivo, la posición de dónde se declaran las filas y las columnas es absoluta (siempre el primer renglón, el primer caracter debe ser valido Integer y luego debe estar separado por un espacio para el siguiente valor Integer, pues uso Scanner.hasNeztInt()).

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
│  - Interfaz gráfica con Swing                  │
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
- **Métodos**:
  - `avanzarGeneracion()`: Calcula la siguiente generación y devuelve si hubo cambios.
  - `contarVecinosVivos(int fila, int col)`: Cuenta celdas vivas adyacentes.
  - `mostrar()`: Imprime el tablero en consola.
  - `setCelda()` / `getCelda()`: Acceso y modificación a celdas específicas.

**Lógica de evolución para la siguiente generación**:
```java
// Para cada celda:
// 1. Contar vecinos vivos
// 2. Calcular siguiente estado (delegado a Estado)
// 3. Guardar el estado previsto en una matriz
// 4. Actualizar el estado al final de la iteración
// 5. Reportar si hubo cambios al comparar ambas matrices
```

#### 2. **Celda.java** - Unidad Atómica
- **Responsabilidad**: Representar una celda y su evolución.
- **Métodos**:
  - `calcularSig(int vecinosVivos)`: Delega a `Estado` para calcular el siguiente estado.
  - `evolucionar()`: Transiciona al siguiente estado.
  - `isViva()`: Consulta si la celda está viva.

#### 3. **Estado.java** - Interfaz
- **Responsabilidad**: Definir de manera abstracta los comportamientos de estado. Luego, cada clase que lo implemente definirá su comportamiento.
- **Métodos**:
  - `boolean isViva()`: ¿Es una celda viva?
  - `char getRepresentacion()`: Símbolo visual
  - `Estado SigEstado(int vecinosVivos)`: Calcula transición

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
- **Métodos**:
  - `iniciar()`: Hace visible la ventana principal.
  - `cargarDesdeArchivo()`: Abre un selector de archivo.
  - `configurarManual()`: Genera un tablero aleatorio.
  - `iniciarBucle()`: Ejecuta la simulación con `Timer`.
  - `avanzarUnaGeneracion()`: Avanza una generación manual.
**Flujo**:
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
- Lanza excepciones en popups.

---

## Principios SOLID Aplicados
### 1. **Principio de Responsabilidad Única**
Cada clase tiene una única responsabilidad bien definida:
- `Tablero` → Gestión del tablero y evolución
- `Celda` → Representación de celda y delegación
- `Estado` → Comportamiento específico del estado
- `VistaJuego` → Presentación e interacción
- `CargadorTablero` → Entrada/Salida

### 2. **Principio Abierto/Cerrado**
El sistema es abierto para extensión pero cerrado para modificación:
- Nuevos estados pueden crearse implementando la interfaz `Estado`
- Sin cambiar código existente en `Tablero` o `Celda`, ni la lógica.
- El cargadorTablero y la vista se actualizan para que el nuevo `Estado` pueda leerse su representación y ser visto por el usuario.

### 3. **Principio de Sustitución de Liskov**
Todas las implementaciones de `Estado` son intercambiables:

### 4. **Principio de Segregación de Interfaces**
La interfaz `Estado` define solo lo necesario:

### 5. **Principio de Inversión de Dependencias**
- `Tablero` depende de la abstracción `Estado`, no de implementaciones
- `Celda` recibe `Estado` en su constructor (inyección)


---

## Cómo Extender el Proyecto
### Agregar un Nuevo Estado
Se quiere agregar `EstadoRobot`:
- Es considerado "vivo"
- Se representa con `R`
- Muere si tiene más de 4 vecinos vivos.

#### Paso 1: Crear la Nueva Clase de Estado
Crear `EstadoRobot.java` en `src/modelo/`:
```java
package modelo;

public class EstadoRobot implements Estado {
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

#### Paso 2: Actualizar el CargadorTablero
Modificar `CargadorTablero.java` en el método `crearEstadoSegunCaracter()`:
```java
private static Estado crearEstadoSegunCaracter(char c) {
    return switch (Character.toUpperCase(c)) {
        case 'O' -> new EstadoVivo();
        case 'E' -> new EstadoEnfermo();
        case 'X' -> new EstadoLatente();
        case 'R' -> new EstadoRobot();    // ← NUEVA LÍNEA
        case '.' -> new EstadoMuerto();
        default  -> new EstadoMuerto();
    };
}
```
#### Paso 3: Actualizar la Vista
Modificar `VistaJuego.java` en el método `colorPorEstado()`:
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
Modificar `VistaJuego.java` en el método `configurarManual()`:
```java
private void configurarManual() {
        // ...
            this.tablero = new Tablero(filas, columnas);

            for (int i = 0; i < filas; i++) {
                for (int j = 0; j < columnas; j++) {
                    Estado inicial;
                    double random = Math.random();
                    if (random < 0.25) {
                        inicial = new EstadoVivo();
                    } else if (random < 0.5) {
                        inicial = new EstadoMuerto();
                    } else if (random < 0.75) {
                        inicial = new EstadoEnfermo();
                    } else if (random < 0.9){
                        inicial = new EstadoLatente();
                    } else {
                        inicial = new EstadoRobot();    //NUEVA LÍNEA -- decidir en qué porcentaje se quiere ver este estado al crear un tablero aleatorio
                    }
                    Celda cInicial = new Celda(inicial);
                    tablero.setCelda(i, j, cInicial);
                }
            }
        // ...
    }
```
---

## Notas Técnicas

### Patrones de Diseño Utilizados
- **State Pattern** (Patrón Estado): `Celda` y `Estado` para transicionar a la siguiente generación. 
- **Template Method (Método Plantilla)**: Flujo de ejecución en `VistaJuego`.
- **Abstract Factory Pattern (Fábrica Abstracta)**: Buso establecer familia de objetos relacionados. Creo `Estado` como abstract factory para describir la familia, y creo una implementación concreta para cada Estado, `EstadoVivo`, `EstadoMuerto`, `EstadoEnfermo`, ...
- **Patrón Strategy**: En tirmpo de ejecución una familia de objetos es intercambiable. Ocurre con `Estado`.

---

**Última actualización**: Septiembre 2026

