# PetCare — Navegación, Configuración e Inyección de dependencias (Persona D)

## 1. Información general
- **Responsable:** Persona D
- **Rama de trabajo:** `feature/persona-d-navegacion-configuracion` (sale de `tarea-c-cuidados`, que ya incluye la rama de Persona B)
- **Módulo:** Hilt, navegación entre pantallas y pantalla de Configuración (RF10)

Este documento describe lo que agregué para unir los módulos de B y C en una app que se puede ejecutar, los cambios que tuve que hacer en sus archivos y el resultado de las pruebas en el emulador.

## 2. Funcionalidades desarrolladas
- Inyección de dependencias con **Hilt** para toda la app (base de datos, DAOs, repositorios, DataStore y ViewModels).
- Creación de la base de datos Room con el callback de tipos de cuidado iniciales de Persona C.
- **Navegación** con `NavHost` y rutas tipadas (kotlinx-serialization), con ids `Long`.
- **Barra inferior** con 3 pestañas: Inicio, Mascotas y Ajustes (solo visible en esas pantallas).
- **Pantalla de Configuración** con DataStore: tema (claro/oscuro), orden de mascotas y orden del historial.
- Pantallas temporales de Splash e Inicio mientras Persona A sube las suyas.

**Estado:** implementado y probado en el emulador (ver §7).

## 3. Arquitectura utilizada
- **Hilt:** `@HiltAndroidApp`, `@AndroidEntryPoint`, `@HiltViewModel`, módulos con `@Provides`.
- **Navigation Compose:** `NavHost`, rutas `@Serializable`, `SavedStateHandle.toRoute()` en los ViewModels.
- **DataStore Preferences:** persistencia de las preferencias de Configuración.
- **StateFlow + `stateIn`:** la pantalla de Configuración refleja siempre lo guardado en DataStore.

## 4. Archivos desarrollados

### Dependencias (Gradle)
- `gradle/libs.versions.toml`, `build.gradle.kts`, `app/build.gradle.kts`: Hilt 2.60.1 (plugin + KSP), hilt-navigation-compose 1.4.0, navigation-compose 2.10.2, kotlinx-serialization 1.9.0, datastore-preferences 1.2.1 y material-icons-extended.

### Inyección de dependencias
- `PetCareApplication.kt`: clase `Application` con `@HiltAndroidApp` (registrada en `AndroidManifest.xml`).
- `MainActivity.kt`: `@AndroidEntryPoint`; muestra `PetCareApp()`.
- `di/DatabaseModule.kt`: crea `PetCareDatabase` (`petcare_database`) con `TiposCuidadoIniciales` y provee los 3 DAOs.
- `di/DataStoreModule.kt`: provee el `DataStore<Preferences>` (`petcare_preferencias`).
- `di/RepositoryModule.kt`: provee `MascotaRepository`, `CuidadoRepository` y `PreferenciasRepository`.

### Preferencias
- `domain/model/UserPreferences.kt`: enums `Tema`, `OrdenMascotas`, `OrdenHistorial` y la data class con valores por defecto.
- `data/repository/PreferenciasRepository.kt` + `PreferenciasRepositoryImpl.kt`: lectura y escritura en DataStore. Si un valor guardado no coincide con ninguna opción, se usa el valor por defecto en lugar de cerrar la app.

### Pantalla Configuración
- `ui/configuracion/ConfiguracionUiState.kt`, `ConfiguracionViewModel.kt`, `ConfiguracionRoute.kt`, `ConfiguracionScreen.kt`.

### Navegación
- `ui/navigation/PetCareRoute.kt`: las 7 rutas tipadas.
- `ui/navigation/PetCareApp.kt`: `Scaffold` + `NavHost` + barra inferior.
- `ui/navigation/PantallasPendientes.kt`: Splash e Inicio temporales. **Persona A:** reemplazar en `PetCareApp.kt` por `SplashRoute`/`HomeRoute` y borrar este archivo.

## 5. Rutas

| Ruta | Pantalla | Parámetros |
| --- | --- | --- |
| `Splash` | Splash (temporal) | — |
| `Home` | Inicio (temporal) | — |
| `Mascotas` | Mis Mascotas (B) | — |
| `MascotaForm` | Registrar/Editar mascota (B) | `mascotaId: Long? = null` (null = registrar) |
| `DetalleMascota` | Detalle de mascota (C) | `mascotaId: Long` |
| `CuidadoForm` | Registrar/Editar cuidado (C) | `mascotaId: Long`, `cuidadoId: Long? = null` (null = registrar) |
| `Configuracion` | Configuración (D) | — |

Flujo: Splash → Inicio (el Splash no queda en el historial). Mascotas → Agregar → formulario → al guardar o cancelar se vuelve. Mascotas → tarjeta → Detalle → Editar mascota / Registrar cuidado / tocar un cuidado. Al eliminar la mascota se vuelve directo a Mascotas.

## 6. Cambios en archivos de otras personas

Persona B autorizó corregir problemas de compatibilidad y Persona C ofreció adaptar sus ViewModels a Hilt; lo hice yo para no duplicar trabajo. **La lógica de las pantallas no cambió.**

### Persona B (`ui/mascotas`, `ui/mascotaform`)
- `MascotasViewModel` y `MascotaFormViewModel`: `@HiltViewModel` + `@Inject constructor`.
- `MascotaFormViewModel` recibe `SavedStateHandle`, lee `mascotaId` de la ruta y llama a `cargarMascota` en `init`.
- `MascotasRoute` y `MascotaFormRoute`: el `viewModel` pasa al **último** parámetro con valor por defecto `hiltViewModel()`. `MascotaFormRoute` ya no recibe `mascotaId` ni tiene el `LaunchedEffect` que cargaba la mascota.

### Persona C (`ui/detallemascota`, `ui/cuidadoform`)
- `DetalleMascotaViewModel` y `CuidadoFormViewModel`: `@HiltViewModel` + `@Inject constructor` + `SavedStateHandle`. `mascotaId` (y `cuidadoId`) ya no van en el constructor: se leen de la ruta.
- `CuidadoFormViewModel` llama a `cargarCuidado` en `init` cuando la ruta trae `cuidadoId`.
- `DetalleMascotaRoute` y `CuidadoFormRoute`: `viewModel` al final con `hiltViewModel()`. `CuidadoFormRoute` ya no recibe `cuidadoId` ni tiene el `LaunchedEffect` de carga.

### Base de datos (B y C)
- Agregué `fallbackToDestructiveMigration(dropAllTables = true)`: la BD está en versión 2 sin migraciones, así que si cambia la versión se recrea en vez de cerrarse la app. **Ya no hace falta desinstalar la app** al cambiar la versión (pero se pierden los datos locales).
- Los `RepositoryImpl` de B y C no se modificaron: se crean con `@Provides` en `RepositoryModule`.

## 7. Estado de verificación

Probado en el emulador `Medium_Phone_API_37.0`. Los datos se verificaron copiando la base de datos del emulador y consultándola.

| Verificación | Estado |
| --- | --- |
| Compilación (`assembleDebug`) sin errores ni warnings | ✅ |
| Splash → Inicio, y "atrás" desde Inicio sale de la app | ✅ |
| Barra inferior: cambio de pestañas, "atrás" desde Ajustes vuelve a Inicio | ✅ |
| Configuración: los cambios persisten tras cerrar la app por completo | ✅ |
| Registrar mascota y verla en la lista | ✅ |
| Validaciones: nombre vacío, especie vacía, peso 0 | ✅ |
| Fecha de nacimiento (se guarda en días desde 1970) | ✅ |
| Búsqueda por nombre (sin distinguir mayúsculas) | ✅ |
| Editar mascota (carga los datos) | ✅ |
| Foto: se guarda y se sigue viendo tras reiniciar la app | ✅ |
| 4 tipos de cuidado iniciales | ✅ |
| Registrar, editar y ver cuidados en el detalle | ✅ |
| Eliminar mascota (con confirmación) → vuelve a la lista | ✅ |
| Eliminación en cascada de sus cuidados (comprobado en la BD) | ✅ |
| Funcionamiento sin conexión (modo avión) | ✅ |

## 8. Observaciones para el equipo
- **Persona A:** las rutas `Splash` y `Home` ya existen. Para el Splash, al terminar navega a `PetCareRoute.Home` con `popUpTo<PetCareRoute.Splash> { inclusive = true }` (así está en `PetCareApp.kt`).
- **Persona B:**
  - ~~Al tocar el texto "Macho"/"Hembra" no se selecciona~~ → corregido: la fila entera es `selectable`.
  - ~~"Gato · " con el separador colgando~~ → corregido en la lista y en el detalle.
- **Preferencias:** se guardan, se leen (RF10) **y se aplican**: el tema en toda la app (incluidos los iconos de la barra de estado), el orden de Mis mascotas (`MascotasViewModel`, por nombre con `Collator` o por más reciente) y el del historial (`DetalleMascotaViewModel`).
- **Room:** al compilar se genera `app/schemas/.../PetCareDatabase/2.json` (por `exportSchema = true`). Falta decidir si se versiona; lo normal es subirlo.
- **Emulador:** si aparece el tutorial de lápiz de Gboard y no deja escribir: `adb shell settings put secure stylus_handwriting_enabled 0`.

## 9. Extras opcionales asignados a Persona A

La rúbrica da 1.0 punto extra por tres criterios que ninguna rama tenía implementados. Se asignan a **Persona A**.
Trabajar sobre la rama `feature/persona-d-navegacion-configuracion` (o `main` cuando se fusione), no sobre ramas
anteriores: ahí ya están Hilt, la navegación y los ViewModels migrados.

| Extra | Puntos | Estado actual | Qué hay que hacer | Archivos principales |
| --- | --- | --- | --- | --- |
| Diseño adaptativo / `WindowSizeClass` | 0.4 | No existe; todo es diseño de teléfono | Calcular el tamaño de ventana (p. ej. `currentWindowAdaptiveInfo()`) y, en pantallas anchas, cambiar la barra inferior por un `NavigationRail` (o usar `NavigationSuiteScaffold`). Opcional: limitar el ancho de formularios en tablet. | `ui/navigation/PetCareApp.kt` (coordinar con D) |
| Ícono adaptativo vectorizado | 0.3 | Solo el ícono de plantilla de Android Studio (robot verde) | Diseñar un ícono propio de PetCare como vector: capas `foreground`, `background` y `monochrome`. Reutilizarlo como logo del Splash. | `res/drawable/ic_launcher_foreground.xml`, `ic_launcher_background.xml`, `res/mipmap-anydpi-v26/ic_launcher*.xml` |
| Capa de dominio / Use Cases | 0.3 | Existe `domain/model/`, pero los ViewModels llaman directo a los repositorios | Crear `domain/usecase/` (p. ej. `ObtenerMascotasUseCase`, `GuardarMascotaUseCase`, `EliminarMascotaUseCase`, `ObtenerCuidadosDeMascotaUseCase`) con `@Inject constructor` y usarlos en los ViewModels en vez del repositorio. | `domain/usecase/` (nuevo) + los ViewModels de B y C (avisarles) |

**Coordinación:**
- `WindowSizeClass` modifica `PetCareApp.kt` (de D) → avisar a D antes de empezar.
- Los Use Cases modifican los ViewModels de B y C → avisarles para que no los toquen a la vez.
- Para el ícono, la carpeta `mipmap-*` con PNG de la plantilla se puede dejar o regenerar con *Image Asset* de Android Studio.
