# RPG por Turnos — UADE POO 2026 — Grupo 03

Juego de rol por turnos desarrollado en Java con interfaz gráfica Swing como proyecto final de la materia **Paradigma Orientado a Objetos** de la Universidad Argentina de la Empresa (UADE).

---

## Tabla de contenidos

1. [Descripción general](#descripción-general)
2. [Cómo compilar y ejecutar](#cómo-compilar-y-ejecutar)
3. [Arquitectura: patrón MVC](#arquitectura-patrón-mvc)
4. [Capa Modelo](#capa-modelo)
   - [Jerarquía de entidades](#jerarquía-de-entidades)
   - [Sistema de ítems y equipamiento](#sistema-de-ítems-y-equipamiento)
   - [Sistema de habilidades](#sistema-de-habilidades)
   - [Gestores del modelo](#gestores-del-modelo)
5. [Capa Controlador](#capa-controlador)
6. [Capa Vista](#capa-vista)
7. [Mecánicas de juego](#mecánicas-de-juego)
8. [Patrones de diseño](#patrones-de-diseño)
9. [Diagramas y documentación](#diagramas-y-documentación)

---

## Descripción general

El juego permite al jugador armar una party de hasta **4 héroes** de distintas clases y enfrentarse a oleadas de enemigos en combates por turnos. Los personajes ganan experiencia, suben de nivel, obtienen ítems como recompensa y pueden guardar el progreso entre sesiones.

**Características principales:**
- 5 clases de personaje jugables con habilidades únicas
- 4 tipos de enemigos con IA propia (incluyendo un boss: el Dragón)
- Sistema de turnos ordenado por velocidad
- Daño físico, mágico e ignorar defensa
- Buffs y debuffs durante el combate
- Inventario, equipo (arma y armadura) y consumibles
- Animaciones de sprites hoja por hoja (sprite sheet)
- Guardado y carga de partida mediante serialización Java

---

## Cómo compilar y ejecutar

### Requisitos
- Java JDK 8 o superior

### Compilación (Windows)
```
compilar.bat
```
El script `compilar.bat` compila todas las clases del proyecto.

### Ejecución
```
java Main
```
El punto de entrada es `Main.java`, que instancia `ControladorJuego` e inicia la ventana Swing.

### Guardar / cargar partida
El guardado se escribe en `partida_guardada.dat` en el directorio raíz del proyecto.

---

## Arquitectura: patrón MVC

El proyecto aplica el patrón **Modelo-Vista-Controlador** con separación clara en tres capas:

```
┌──────────────────────────────────────────────────────────┐
│                     VISTA (View)                          │
│  PantallaInicio · PantallaCreacionParty                  │
│  PantallaBatalla · PantallaResultados                    │
│  PanelArena · GestorSprites · SpriteHoja                 │
├──────────────────────────────────────────────────────────┤
│                  CONTROLADOR (Controller)                 │
│  ControladorJuego · ControladorBatalla                   │
├──────────────────────────────────────────────────────────┤
│                     MODELO (Model)                        │
│  Entidades · Ítems · Habilidades · Gestores              │
│  Party · Partida · Batalla · Inventario                  │
└──────────────────────────────────────────────────────────┘
```

La Vista nunca accede directamente al Modelo; toda interacción pasa por el Controlador.

---

## Capa Modelo

### Jerarquía de entidades

```
Entidad (abstracta)
├── Personaje (abstracta)
│   ├── Guerrero
│   ├── Mago
│   ├── Arquero
│   ├── Curandero
│   └── Tanque
└── Enemigo (abstracta)
    ├── Goblin
    ├── Slime
    ├── Esqueleto
    └── Dragon
```

#### `Entidad` (clase base abstracta)

| Atributo | Tipo | Descripción |
|---|---|---|
| `nombre` | String | Nombre de la entidad |
| `vidaActual` / `vidaMaxima` | int | Puntos de vida |
| `ataque` | int | Valor de ataque base |
| `defensa` | int | Valor de defensa base |
| `velocidad` | int | Determina el orden de turnos |
| `nivel` | int | Nivel actual |
| `enDefensa` | boolean | Estado de defensa activa |

Métodos clave: `recibirDaño()`, `curar()`, `estaVivo()`, `activarDefensa()`, `desactivarDefensa()`.

---

#### `Personaje` (abstracta, extiende `Entidad`)

Agrega el sistema de maná, experiencia, habilidades y equipamiento.

| Atributo | Descripción |
|---|---|
| `manaActual` / `manaMaxima` | Puntos de maná para usar habilidades |
| `experiencia` / `expParaSiguienteNivel` | Sistema de progresión por niveles |
| `habilidades[]` | Arreglo de hasta 2 habilidades propias de la clase |
| `armaEquipada` / `armaduraEquipada` | Equipamiento activo |

Al subir de nivel (`subirNivel()`): **+15 HP máx., +10 Maná máx., +3 ATK, +2 DEF, +1 VEL**.

---

#### Clases de personaje jugables

| Clase | HP | Maná | ATK | DEF | VEL | Habilidades |
|---|---|---|---|---|---|---|
| **Guerrero** | 130 | 30 | 25 | 15 | 10 | Golpe Fuerte · Grito de Batalla |
| **Mago** | 80 | 90 | 15 | 8 | 13 | Bola de Fuego · Rayo Helado |
| **Arquero** | 95 | 50 | 22 | 10 | 16 | Disparo Crítico · Lluvia de Flechas |
| **Curandero** | 90 | 80 | 12 | 12 | 11 | Curar · Bendición |
| **Tanque** | 160 | 40 | 18 | 20 | 7 | Defensa Fortificada · Golpe Escudo |

---

#### `Enemigo` (abstracta, extiende `Entidad`)

Agrega `experienciaOtorgada` y `oroOtorgado`. Cada subclase implementa `elegirObjetivo(Party)` con su propia IA de selección de blanco.

| Enemigo | HP | ATK | DEF | VEL | EXP | Oro | Estrategia de blanco |
|---|---|---|---|---|---|---|---|
| **Goblin** | 70 | 22 | 5 | 14 | 40 | 10 | Aliado con menor HP actual |
| **Slime** | 50 | 18 | 2 | 9 | 30 | 8 | Aleatorio |
| **Esqueleto** | 75 | 18 | 10 | 9 | 65 | 15 | Aleatorio |
| **Dragon** | 300 | 35 | 20 | 8 | 250 | 60 | Aliado con mayor HP · soplo cada 3 turnos |

---

### Sistema de ítems y equipamiento

```
Item (abstracta)
├── Equipable (abstracta)
│   ├── Arma          → bonus de ataque
│   └── Armadura      → bonus de defensa
└── Consumible (abstracta)
    ├── Pocion        → restaura HP
    └── PocianMana    → restaura Maná (solo a Personaje)
```

El **`Inventario`** es una estructura que agrupa ítems de la party y permite filtrarlos por tipo (`getConsumibles()`, `getEquipables()`).

---

### Sistema de habilidades

Todas las habilidades extienden la clase abstracta `Habilidad` e implementan:

```java
void ejecutar(Entidad origen, Entidad objetivo, GestorCombate gestor)
```

El enum `TipoObjetivo` define el alcance: `ENEMIGO`, `ALIADO`, `TODOS_ENEMIGOS`, `YO_MISMO`.

| Habilidad | Clase | Coste MP | Tipo | Efecto |
|---|---|---|---|---|
| Golpe Fuerte | Guerrero | 15 | ENEMIGO | 180% daño físico |
| Grito de Batalla | Guerrero | 20 | YO_MISMO | +8 ATK propio |
| Bola de Fuego | Mago | 30 | TODOS_ENEMIGOS | 130% daño mágico a todos |
| Rayo Helado | Mago | 20 | ENEMIGO | 140% daño mágico + −3 VEL |
| Disparo Crítico | Arquero | 20 | ENEMIGO | 200% daño ignorando defensa |
| Lluvia de Flechas | Arquero | 25 | TODOS_ENEMIGOS | 90% daño físico a todos |
| Curar | Curandero | 20 | ALIADO | Restaura 30 + 10×nivel HP |
| Bendición | Curandero | 15 | ALIADO | +10 DEF al objetivo |
| Defensa Fortificada | Tanque | 25 | YO_MISMO | +25 DEF + modo defensa |
| Golpe Escudo | Tanque | 18 | ENEMIGO | 150% daño físico + +8 DEF propio |

---

### Gestores del modelo

| Clase | Responsabilidad |
|---|---|
| `GestorCombate` | Orquesta el combate: orden de turnos, acciones de jugador y enemigo, cálculo de daño, buffs/debuffs y verificación de fin de batalla |
| `GestorRecompensas` | Distribuye XP equitativamente, genera loot aleatorio escalado al escenario y restaura 30% de vida tras la victoria |
| `GestorPersistencia` | Serializa y deserializa la `Partida` al archivo `partida_guardada.dat` |
| `CatalogoJuego` | Singleton que genera los encuentros de enemigos por número de escenario (stats escalan a partir del escenario 6) |

**`Partida`** es el objeto raíz del estado de juego: contiene la `Party`, el número de escenario actual, el oro acumulado y el historial de `Batalla`.

---

## Capa Controlador

### `ControladorJuego`

Controlador principal de la aplicación. Gestiona la navegación entre pantallas con **`CardLayout`** y coordina las transiciones del flujo de juego.

Flujo principal:

```
PantallaInicio
    │
    ├── Nueva partida → PantallaCreacionParty → iniciarBatalla()
    │                                                │
    └── Cargar partida ──────────────────────────────┘
                                                     │
                                              PantallaBatalla
                                                     │
                                         onBatallaTerminada()
                                                     │
                                         PantallaResultados
                                         ├── Siguiente batalla → PantallaBatalla
                                         ├── Guardar
                                         └── Menú → PantallaInicio
```

### `ControladorBatalla`

Gestiona el ciclo de turnos dentro de un combate:

1. `iniciarBatalla()` — configura el `GestorCombate` y genera el orden de turnos.
2. `procesarSiguienteTurno()` — avanza al siguiente turno; si es de un personaje, habilita los botones de acción; si es de un enemigo, ejecuta la IA.
3. Acciones del jugador: `accionAtacar()`, `accionDefender()`, `accionHabilidad()`, `accionItem()`.
4. `terminarBatalla()` — detecta victoria/derrota y notifica al `ControladorJuego`.

---

## Capa Vista

### Pantallas principales

| Clase | Descripción |
|---|---|
| `PantallaInicio` | Pantalla título con sprites animados y botones "Nueva Partida" / "Cargar Partida" |
| `PantallaCreacionParty` | Selección de hasta 4 héroes con dropdown de clase, panel de stats en tiempo real y preview de sprite |
| `PantallaBatalla` | Pantalla principal de combate: arena central, barras de HP/Maná, log de combate y botones de acción |
| `PantallaResultados` | Resumen post-batalla: EXP ganada, subidas de nivel, ítems obtenidos y oro |

### Sistema de sprites y animación

**`GestorSprites`** (Singleton) extrae fotogramas del sprite sheet `recursos/nuevos sprites.png` (1024×1536 px).

| Personaje | Fila IDLE (y) | Fila ATAQUE (y) |
|---|---|---|
| Curandero | 20 | 130 |
| Mago | 257 | 363 |
| Tanque | 467 | 572 |
| Guerrero | 678 | 786 |
| Arquero | 895 | 1005 |
| Goblin | 1117 | — |
| Slime | 1235 | — |

**`PanelArena`** renderiza los sprites (80×96 px en pantalla) sobre el fondo `recursos/fondo.png` a **8 FPS** (125 ms por fotograma). Admite selección de objetivo con el mouse y maneja las siguientes animaciones mediante el enum `TipoAnimacion`:

| Estado | Descripción |
|---|---|
| `IDLE` | Animación de espera |
| `ATAQUE` | El sprite avanza hacia el objetivo e impacta |
| `RECIBE_DAÑO` | Fotograma de impacto |
| `CURAR` | Fotograma de curación |
| `MUERTE` | Fotograma final, entidad se retira de la arena |

---

## Mecánicas de juego

### Orden de turnos

Los turnos se ordenan de mayor a menor **Velocidad (VEL)** al inicio de cada ronda. Las entidades muertas son omitidas automáticamente.

### Fórmulas de daño

**Daño físico:**
```
base = max(1, ATK_atacante − DEF_defensor)     ← DEF completa si está en defensa, DEF/2 si no
varianza = max(1, base × 0.15)
daño = (base ± aleatorio) × multiplicador
final = max(1, daño)
```

**Daño mágico:**
```
base = max(1, ATK_atacante + nivel×3 − DEF_defensor/3)
varianza = max(1, base × 0.20)
daño = (base ± aleatorio) × multiplicador
final = max(1, daño)
```

**Ignorar defensa (Disparo Crítico):**
```
base = ATK_atacante
varianza = max(1, base × 0.10)
daño = (base ± aleatorio) × multiplicador
final = max(1, daño)
```

### Sistema de progresión

- La EXP requerida para el siguiente nivel crece **×1.5** por nivel.
- Al subir de nivel: **+15 HP máx., +10 Maná máx., +3 ATK, +2 DEF, +1 VEL**.
- La EXP de batalla se distribuye equitativamente entre todos los personajes vivos de la party.

### Sistema de loot (por escenario)

| Objeto | Probabilidad | Condición |
|---|---|---|
| Poción (40 HP) | 50% | Siempre |
| Poción de Maná (30 MP) | 20% | Siempre |
| Poción Grande (80 HP) | 10% | Siempre |
| Arma (+5+escenario ATK) | 10% | Escenario ≥ 3 |
| Armadura (+4+escenario DEF) | 5% | Escenario ≥ 2 |

Tras la victoria también se restaura el **30% de HP** de cada personaje vivo.

---

## Patrones de diseño

| Patrón | Dónde se aplica |
|---|---|
| **MVC** | Separación total entre `modelo/`, `controlador/` y `vista/` |
| **Singleton** | `GestorSprites`, `CatalogoJuego` |
| **Herencia + Polimorfismo** | `Entidad → Personaje/Enemigo`, `Item → Equipable/Consumible`, `Habilidad` |
| **Strategy** | Cada subclase de `Habilidad` implementa su propio `ejecutar()` ; cada `Enemigo` implementa su propia `elegirObjetivo()` |
| **Template Method** | `Habilidad` define el contrato abstracto; las subclases concretan el comportamiento |
| **Observer / Listener** | Listeners de Swing conectan botones de la vista con acciones del controlador |
| **Serialization** | `GestorPersistencia` guarda y carga la `Partida` completa con `ObjectOutputStream` |

---

## Diagramas y documentación

| Archivo | Descripción | Cómo abrirlo |
|---|---|---|
| `UML.drawio` | Diagrama de clases completo | [diagrams.net](https://app.diagrams.net/) → File → Import from → Device |
| `Sequence diagram Experiencia.txt` | Diagrama de secuencia: distribución de EXP | Copiar y pegar en [sequencediagram.org](https://sequencediagram.org/) |
| `Sequence diagram Habilidad.txt` | Diagrama de secuencia: ejecución de habilidad | Copiar y pegar en [sequencediagram.org](https://sequencediagram.org/) |
| `Final.png` | Captura: pantalla de resultados | — |
| `Menu.png` | Captura: menú principal | — |
| `Pelea.png` | Captura: combate en curso | — |
| `Personajes.png` | Captura: creación de party | — |
