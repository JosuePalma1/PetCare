# Memoria — Persona D (Configuración + Navegación + Hilt)

> Bitácora personal. Vive en la rama huérfana `notas/persona-d`, **nunca** se fusiona a `main`.
> Última actualización: 2026-10-11

## Cómo usar este archivo en otro dispositivo

```bash
git clone https://github.com/JosuePalma1/PetCare.git   # o git fetch si ya está clonado
cd PetCare
echo ".notas/" >> .git/info/exclude                       # que git del proyecto ignore la carpeta
git worktree add .notas notas/persona-d                   # monta la rama de notas en .notas/
```

Guardar cambios:

```bash
cd .notas && git add -A && git commit -m "notas: ..." && git push
```

(Primera vez en un equipo nuevo, si `git push` pide upstream: `git push -u origin notas/persona-d`.)

---

## Mi responsabilidad (según `docs/plan-tecnico-implementacion.md`)

- `ConfiguracionScreen` + `ConfiguracionViewModel` (RF10, wireframe "Pantalla 9") con DataStore.
- `UserPreferences` (enums `Tema`, `OrdenMascotas`, `OrdenHistorial`) + `PreferenciasRepository` + impl.
- `PetCareRoute` (7 rutas tipadas), `PetCareApp` (NavHost + bottom bar Inicio/Mascotas/Ajustes).
- Hilt completo: `PetCareApplication` (@HiltAndroidApp), `MainActivity` (@AndroidEntryPoint),
  `DatabaseModule`, `RepositoryModule`, `DataStoreModule`, `AndroidManifest`.
- Dependencias Gradle: Hilt, hilt-navigation-compose, Navigation Compose, kotlinx-serialization, DataStore.

## Estado del equipo (2026-10-11)

| Persona | Rama | Estado |
|---|---|---|
| A — Splash + Home | (sin rama todavía) | Pendiente → usaré placeholders en el NavHost |
| B — Mascotas + Form mascota (JosuePalma1) | `feature/persona-b-mascotas-crud` | Subido. Documentó en `docs/PERSONA_B_MASCOTAS.md` |
| C — Detalle + Form cuidado (yimmileo) | `tarea-c-cuidados` | Subido, **construido encima de la rama de B** |
| D — yo | `feature/persona-d-navegacion-configuracion` | Rama creada, dependencias añadidas |

## Convenciones reales del proyecto (mandan sobre el plan)

- **Paquetes**:
  - `data/local/entity/XEntity.kt`, `data/local/dao/XDao.kt`, `data/local/PetCareDatabase.kt`
  - `data/repository/XRepository.kt` + `XRepositoryImpl.kt` (no `OfflineX` como dice el plan)
  - `domain/model/X.kt` (modelo de dominio, mapeado desde la Entity)
  - `ui/<feature>/` con `XRoute.kt`, `XScreen.kt`, `XUiState.kt`, `XViewModel.kt`
- **IDs son `Long`** (no `Int` como en el plan) → rutas tipadas con `Long`.
- **Commits**: Conventional Commits en español: `feat(scope): ...`, `build(scope): ...`, `fix(build): ...`, `refactor(di): ...`.
- **Ramas**: `feature/persona-x-descripcion` (C no lo siguió, pero es el patrón más claro).
- **Docs**: cada persona deja `docs/PERSONA_X_*.md` → yo haré `docs/PERSONA_D_NAVEGACION_CONFIGURACION.md`.
- Versiones (Gradle catalog): AGP 9.3.0, Kotlin 2.2.10, KSP 2.3.10, Room 2.8.5, Lifecycle 2.11.0, Compose BOM 2026.02.01.

## Decisiones tomadas

1. **Rama base**: `feature/persona-d-navegacion-configuracion` sale de `origin/tarea-c-cuidados` (trae B + C).
   Si B o C reescriben su historial → rebasear.
2. **ViewModels de B y C → `@HiltViewModel`**: añadir `@HiltViewModel` + `@Inject constructor`,
   y leer `mascotaId`/`cuidadoId` desde `SavedStateHandle.toRoute<PetCareRoute.X>()` en vez de pasarlos
   por constructor / `LaunchedEffect`. Sus `Route` pasan a `viewModel: XViewModel = hiltViewModel()`.
   Hacerlo en commits separados `refactor(di): ...` y **avisar a B y C**.
3. **Memoria**: este archivo en rama huérfana `notas/persona-d`, montada como worktree en `.notas/`
   (excluida vía `.git/info/exclude`, no en `.gitignore`).
4. **Identidad git**: en este repo uso el correo noreply de GitHub
   `240799525+DumarMatamoros@users.noreply.github.com` (config local) y `gh auth setup-git` para credenciales.
   Claude **no** ejecuta commits/pull/push: me da los comandos y yo los corro.

## Lo que B pidió a D (mensaje + `docs/PERSONA_B_MASCOTAS.md` §6)

B autorizó explícitamente: *"si encuentran algún problema de compatibilidad, corríjanlo ustedes"* → cubre el `refactor(di)`.

- [x] Crear `PetCareDatabase` → `DatabaseModule`
- [x] Proveer `MascotaDao` y `MascotaRepository` → `DatabaseModule` / `RepositoryModule`
- [x] Inyección de los ViewModels → `@HiltViewModel`
- [x] Conectar `MascotasRoute` con registro (`onAgregarMascota`) y detalle (`onMascotaClick`) → NavHost
- [x] Conectar `MascotaFormRoute`: `onCancelar` / `onGuardadoExitoso` → `popBackStack()`
- [x] `mascotaId: Long?` al formulario → ahora viaja en `PetCareRoute.MascotaForm` (no como parámetro)
- Al probar: usar la lista de pruebas de su §9 (registro, validaciones, búsqueda, edición, foto tras reiniciar,
  eliminar en cascada, offline). Ojo: su doc dice que la BD tiene 1 entidad, pero C ya la amplió a 3 (v2).

## Lo que C pidió a D (mensaje de C)

C ofreció adaptar él mismo sus ViewModels a Hilt y encargarse de las rutas de Detalle/Cuidado → **ya lo hice yo**;
avisarle para que no duplique trabajo.

- [x] `databaseBuilder` + `addCallback(TiposCuidadoIniciales)` → `DatabaseModule` (nombre `petcare_database`; C sugirió `petcare.db`, da igual)
- [x] `CuidadoRepositoryImpl(cuidadoDao, tipoCuidadoDao)` provisto → `RepositoryModule`
- [x] `DetalleMascotaViewModel` / `CuidadoFormViewModel` con id de la ruta → Hilt + `SavedStateHandle`
- [x] NavHost con ids `Long`:
  - Detalle `onEditarMascota` → `MascotaForm(mascotaId)`
  - Detalle `onRegistrarCuidado` → `CuidadoForm(mascotaId)`; `onCuidadoClick(id)` → `CuidadoForm(mascotaId, id)`
  - Detalle `onMascotaEliminada` → volver a la lista (`popBackStack` hasta `Mascotas`)
- C dice que con BD v2 hay que desinstalar la app del emulador; con `fallbackToDestructiveMigration` ya no hace falta.
- Pruebas de C: crear/editar/eliminar cuidados, eliminar mascota con cascada, persistencia tras cerrar la app.

## Puntos a resolver / riesgos detectados

- ~~`RepositoryImpl` de B/C no tienen `@Inject`~~ → resuelto con `@Provides` en `RepositoryModule` (no toco sus archivos).
- No existe `Room.databaseBuilder` → lo creo en `DatabaseModule`. `PetCareDatabase` está en `version = 2`
  con `exportSchema = true`; revisar si hace falta `fallbackToDestructiveMigration` (no hay migraciones).
  C tiene `TiposCuidadoIniciales.kt` → ver cómo se siembran (¿callback de Room?).
- Firmas actuales de las Route (para el NavHost):
  - `MascotasRoute(viewModel, onAgregarMascota, onMascotaClick: (Long) -> Unit)`
  - `MascotaFormRoute(viewModel, mascotaId: Long?, onCancelar, onGuardadoExitoso)`
  - `DetalleMascotaRoute(viewModel, onEditarMascota, onRegistrarCuidado, onCuidadoClick: (Long) -> Unit, onMascotaEliminada, onVolver)`
  - `CuidadoFormRoute(viewModel, cuidadoId: Long?, onCancelar, onListo)`
- B usa `material-icons-core` como string literal en `build.gradle.kts`; para `Icons.Default.Pets` hace falta
  `material-icons-extended` (o un ícono propio).
- Nombres reales de pantallas ≠ plan: `MascotaForm*` (plan: `RegistrarMascota*`), `CuidadoForm*` (plan: `RegistrarCuidado*`).

## Próximos pasos

- [x] Crear rama `feature/persona-d-navegacion-configuracion` desde `origin/tarea-c-cuidados`
- [x] `build(di)`: Hilt + KSP, Navigation Compose, kotlinx-serialization, DataStore, hilt-navigation-compose
- [x] `feat(configuracion)`: UserPreferences + PreferenciasRepository(Impl) + DataStore
- [x] `feat(di)`: PetCareApplication, DatabaseModule, RepositoryModule, DataStoreModule, Manifest, MainActivity
- [x] `refactor(di)`: ViewModels de B y C a `@HiltViewModel` + SavedStateHandle (PetCareRoute adelantado)
- [x] `feat(configuracion)`: ConfiguracionScreen/ViewModel/Route/UiState
- [x] `feat(navegacion)`: PetCareRoute + PetCareApp (NavHost + bottom bar) con placeholders de Splash/Home
- [x] Compilar, probar flujo completo, escribir `docs/PERSONA_D_NAVEGACION_CONFIGURACION.md`
- [ ] PR a `main` — **pospuesto hasta que A termine** (decisión 2026-10-11). Al hacerlo: preguntar si el profe pide PR por persona;
  si no, un solo PR desde la rama final. **Nunca "Squash and merge"** (borra autoría de B/C y rompe la cadena) → usar "Create a merge commit".
- [ ] **Al final**: redactar resumen de todos los cambios para A, B y C (sobre todo lo que toqué en sus archivos:
  ViewModels con Hilt, Routes sin `viewModel`/ids, `fallbackToDestructiveMigration`, `2.json` de Room, nombres de rutas)

## Bitácora

- **2026-10-11** — Análisis de ramas de B y C, definición de convenciones y decisiones. Creada rama de notas.
- **2026-10-11** — Identidad git pasa a correo noreply de GitHub; se reescriben los 2 commits iniciales de notas.
- **2026-10-11** — Creada rama `feature/persona-d-navegacion-configuracion` desde `073d6fa`. Commit `build(di)`:
  Hilt 2.60.1 (plugin + KSP), hilt-navigation-compose 1.4.0, navigation-compose 2.10.2,
  kotlinx-serialization 1.9.0 (no 1.10+, que podría requerir Kotlin 2.3), datastore-preferences 1.2.1,
  material-icons-extended (vía BOM). `assembleDebug` OK con AGP 9.3 / Kotlin 2.2.10.
- **2026-10-11** — Commit `feat(configuracion)`: `domain/model/UserPreferences.kt` (enums + data class),
  `data/repository/PreferenciasRepository.kt` + `PreferenciasRepositoryImpl.kt` (`@Inject constructor`, recibe
  `DataStore<Preferences>`; claves string con `enum.name`; valor desconocido → default en vez de `valueOf` que crashea).
  El `DataStore` se provee en `DataStoreModule` (siguiente paso). Nota: el commit `bbd242e` salió sin prefijo `build(di):`.
  Pendiente decidir: `app/schemas/.../2.json` que genera Room (exportSchema de B/C) sin trackear.
- **2026-10-11** — Rebase para renombrar `bbd242e` no aplicado → se queda sin prefijo (aceptado).
- **2026-10-11** — Commit `feat(di)`: `PetCareApplication` (@HiltAndroidApp) + `android:name` en Manifest,
  `MainActivity` @AndroidEntryPoint (aún con el Greeting de plantilla), `di/DatabaseModule` (Room
  `petcare_database` + callback `TiposCuidadoIniciales` + `fallbackToDestructiveMigration(dropAllTables = true)`
  porque v2 no tiene migraciones; DAOs), `di/DataStoreModule` (`petcare_preferencias`),
  `di/RepositoryModule` (object con `@Provides` para los 3 repos). `assembleDebug` OK.
- **2026-10-11** — `ui/navigation/PetCareRoute.kt` creado antes de lo previsto (lo necesita `toRoute`). Rutas con
  nombres reales: `DetalleMascota(mascotaId: Long)`, `MascotaForm(mascotaId: Long? = null)`,
  `CuidadoForm(mascotaId: Long, cuidadoId: Long? = null)` + `Splash`, `Home`, `Mascotas`, `Configuracion`.
- **2026-10-11** — `refactor(di)` en archivos de B y C:
  - 4 ViewModels → `@HiltViewModel` + `@Inject constructor`. Los de formulario/detalle reciben `SavedStateHandle`
    y leen ids con `toRoute<PetCareRoute.X>()`; MascotaForm y CuidadoForm llaman `cargarX(id)` en `init`.
  - 4 Routes: `viewModel: X = hiltViewModel()` pasa al **último** parámetro; se quitan `mascotaId`/`cuidadoId`
    y los `LaunchedEffect` que cargaban. `hiltViewModel` importado de `androidx.hilt.lifecycle.viewmodel.compose`
    (el de `hilt.navigation.compose` está deprecado en 1.3+).
- **2026-10-11** — `ui/configuracion/`: UiState (Loading/Success), ViewModel (`@HiltViewModel`, `stateIn`
  WhileSubscribed(5_000) desde DataStore), Route (`hiltViewModel()`), Screen (estilo de B: título 24sp bold,
  3 Cards con grupos de RadioButton accesibles — fila entera `selectable`, `Role.RadioButton`). No había imagen
  del wireframe "Pantalla 9" en el repo → diseño propio; validar con el equipo. Sin probar en pantalla aún
  (hasta tener el NavHost).
- **2026-10-11** — `feat(navegacion)`: `ui/navigation/PetCareApp.kt` (Scaffold + NavHost 7 rutas + NavigationBar
  Inicio/Mascotas/Ajustes, visible solo en esas 3; pestañas con `popUpTo<Home>{saveState}` + `launchSingleTop` +
  `restoreState`; `volverDesde(entry)` evita doble pop; padding + `consumeWindowInsets` + `imePadding`),
  `ui/navigation/PantallasPendientes.kt` (`SplashPendiente` 1 s → Home sin volver atrás, `HomePendiente`) — borrar
  cuando A suba lo suyo. `MainActivity` ya muestra `PetCareApp()` (fuera el Greeting).
- **2026-10-11** — **Probado en emulador** (Medium_Phone_API_37.0): Splash→Inicio ✅; Ajustes cambia y **persiste tras
  force-stop** ✅; registrar mascota → vuelve a lista ✅; detalle con datos ✅; 4 tipos de cuidado sembrados ✅;
  registrar cuidado → aparece en detalle ✅; editar cuidado carga datos ✅; editar mascota carga datos ✅; eliminar
  mascota (con diálogo) → vuelve a lista vacía ✅; atrás: Ajustes→Inicio→sale ✅; teclado no tapa el formulario ✅.
  Sin verificar a nivel BD el CASCADE (emulador sin `sqlite3`), pero la FK lo declara. No probado: foto, fecha
  de nacimiento, validaciones, búsqueda, modo offline.
  Truco emulador: desactivar tutorial de stylus de Gboard → `adb shell settings put secure stylus_handwriting_enabled 0`.
- **2026-10-11** — Pruebas restantes de B, todas ✅: validaciones (nombre, especie, peso 0), fecha de nacimiento
  (guardada 20731 = 05/10/2026 en días), búsqueda sin mayúsculas, foto persiste tras force-stop, offline (modo avión).
  **CASCADE verificado en BD**: se copia la BD con `adb exec-out run-as com.am.septimob.petcare cat databases/<f>`
  (+ `-wal`, `-shm`) y se consulta con `sqlite3` de Python en el PC (no hace falta instalar sqlite3 en el emulador).
  Hallazgos para B: label "Macho/Hembra" no seleccionable (solo el círculo) → sexo queda ''; tarjeta "Gato · " sin raza.
- **2026-10-11** — Escrito `docs/PERSONA_D_NAVEGACION_CONFIGURACION.md` (incluye §6 con los cambios en archivos
  de B y C → sirve de base para el resumen al equipo).
- **2026-10-11** — Revisadas todas las ramas: **ninguna** tiene WindowSizeClass, ícono propio (solo el de plantilla) ni
  Use Cases (solo `domain/model`). Decisión del usuario: **los 3 extras (+1.0 pt) los hace Persona A**. Documentado en
  `docs/plan-tecnico-implementacion.md` (Fuera de alcance) y `docs/PERSONA_D_NAVEGACION_CONFIGURACION.md` §9.
  A debe avisarme antes de tocar `PetCareApp.kt` (WindowSizeClass) y avisar a B/C por los ViewModels (Use Cases).
- **2026-10-11** — Commit del `2.json` de Room (B subió `1.json` en `d1189dc`; C subió a v2 sin el json).
  PR pospuesto hasta que A termine. Mientras tanto, todo el equipo debe partir de mi rama: A crea su rama desde
  `feature/persona-d-navegacion-configuracion`; B y C hacen sus arreglos ahí o en ramas nuevas desde ella.
