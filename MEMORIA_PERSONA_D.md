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

## Puntos a resolver / riesgos detectados

- `RepositoryImpl` de B/C no tienen `@Inject` → o se lo añado (con el refactor) o uso `@Provides` en el módulo.
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
- [ ] `feat(configuracion)`: UserPreferences + PreferenciasRepository(Impl) + DataStore
- [ ] `feat(di)`: PetCareApplication, DatabaseModule, RepositoryModule, DataStoreModule, Manifest, MainActivity
- [ ] `refactor(di)`: ViewModels de B y C a `@HiltViewModel` + SavedStateHandle
- [ ] `feat(configuracion)`: ConfiguracionScreen/ViewModel/Route/UiState
- [ ] `feat(navegacion)`: PetCareRoute + PetCareApp (NavHost + bottom bar) con placeholders de Splash/Home
- [ ] Compilar, probar flujo completo (ver Paso 2 del plan), escribir `docs/PERSONA_D_...md`, PR a `main`

## Bitácora

- **2026-10-11** — Análisis de ramas de B y C, definición de convenciones y decisiones. Creada rama de notas.
- **2026-10-11** — Identidad git pasa a correo noreply de GitHub; se reescriben los 2 commits iniciales de notas.
- **2026-10-11** — Creada rama `feature/persona-d-navegacion-configuracion` desde `073d6fa`. Commit `build(di)`:
  Hilt 2.60.1 (plugin + KSP), hilt-navigation-compose 1.4.0, navigation-compose 2.10.2,
  kotlinx-serialization 1.9.0 (no 1.10+, que podría requerir Kotlin 2.3), datastore-preferences 1.2.1,
  material-icons-extended (vía BOM). `assembleDebug` OK con AGP 9.3 / Kotlin 2.2.10.
