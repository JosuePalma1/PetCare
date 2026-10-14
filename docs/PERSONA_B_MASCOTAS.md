# PetCare — Módulo de Mascotas (Persona B)

## 1. Información general
- **Responsable:** Persona B
- **Rama de trabajo:** `feature/persona-b-mascotas-crud`
- **Módulo:** Gestión de mascotas

Este documento describe el estado de desarrollo del módulo de mascotas de PetCare, incluyendo los componentes implementados, su arquitectura, las funcionalidades disponibles a nivel de código y los aspectos pendientes de integración.

## 2. Funcionalidades desarrolladas
Se implementaron los siguientes componentes:
- Registro de mascotas.
- Consulta de mascotas registradas.
- Búsqueda reactiva por nombre.
- Carga de información de una mascota para su edición.
- Actualización de los datos de una mascota.
- Operación de eliminación disponible en el DAO y repositorio.
- Validación de nombre, especie y peso.
- Selección de fecha de nacimiento mediante calendario.
- Selección de fotografía y almacenamiento de su URI.
- Visualización de la fotografía seleccionada.
- Indicadores de carga y guardado.
- Manejo de errores y evento de guardado exitoso.

**Estado:** funcionalidades implementadas a nivel de código. Se encuentran pendientes las pruebas funcionales con la aplicación integrada.

## 3. Arquitectura utilizada
El módulo sigue el patrón MVVM y utiliza las siguientes tecnologías:
- **Kotlin y Jetpack Compose:** interfaces de usuario.
- **Material 3:** componentes visuales.
- **ViewModel:** gestión de estado y lógica del formulario.
- **StateFlow:** actualización reactiva de la interfaz.
- **Flow:** observación de datos desde Room.
- **Room:** persistencia local.
- **Repository:** separación entre la capa de datos y los ViewModels.
- **Channel:** comunicación de eventos de guardado exitoso.

Las pantallas visuales reciben estados y funciones mediante parámetros, sin depender directamente de los ViewModels.

## 4. Archivos desarrollados

### Capa de datos
- `data/local/entity/MascotaEntity.kt`: representa la tabla mascotas.
- `data/local/dao/MascotaDao.kt`: contiene operaciones de consulta, inserción, actualización y eliminación.
- `data/local/PetCareDatabase.kt`: declara la base de datos Room. Actualmente incluye únicamente `MascotaEntity`.
- `data/repository/MascotaRepository.kt`: interfaz del repositorio.
- `data/repository/MascotaRepositoryImpl.kt`: implementación del repositorio utilizando `MascotaDao`.
- `domain/model/Mascota.kt`: modelo de mascota utilizado fuera de Room.

### Pantalla Mis Mascotas
- `ui/mascotas/MascotasUiState.kt`: estados de carga, éxito y error.
- `ui/mascotas/MascotasViewModel.kt`: consulta y filtrado reactivo por nombre.
- `ui/mascotas/MascotasRoute.kt`: conecta ViewModel y pantalla.
- `ui/mascotas/MascotasScreen.kt`: buscador, listado y tarjetas seleccionables.

### Formulario Registrar/Editar mascota
- `ui/mascotaform/MascotaFormUiState.kt`: estado de los campos y operaciones.
- `ui/mascotaform/MascotaFormViewModel.kt`: validaciones, registro, carga y actualización.
- `ui/mascotaform/MascotaFormRoute.kt`: conexión con el ViewModel, recepción de `mascotaId` y evento de guardado exitoso.
- `ui/mascotaform/MascotaFormScreen.kt`: formulario visual, calendario, fotografía y botones.

## 5. Contrato de datos
La entidad `Mascota` utiliza los siguientes campos:

| Campo | Tipo |
| --- | --- |
| `id` | `Long` |
| `nombre` | `String` |
| `especie` | `String` |
| `raza` | `String` |
| `fechaNacimiento` | `Long?` |
| `sexo` | `String` |
| `pesoKg` | `Double?` |
| `fotoUri` | `String?` |

**Importante:** `fechaNacimiento` representa días desde el 1 de enero de 1970, no milisegundos. El formulario realiza la conversión desde el selector de fechas.

## 6. Integración con el equipo

### Persona C — Cuidados y detalle de mascotas
- Utilizar el identificador de mascotas de tipo `Long`.
- Coordinar la relación de `Cuidado` con `MascotaEntity`.
- Integrar las entidades `TipoCuidado` y `Cuidado` en `PetCareDatabase`.
- Coordinar las operaciones de edición y eliminación desde el detalle de mascota.
- Revisar el comportamiento de eliminación en cascada.

### Persona D — Hilt y navegación
- Configurar la creación de `PetCareDatabase`.
- Proporcionar `MascotaDao` y `MascotaRepository`.
- Configurar la inyección de dependencias de los ViewModels.
- Conectar `MascotasRoute` con las rutas de registro y detalle.
- Conectar `MascotaFormRoute` con la navegación.
- Enviar `mascotaId: Long?` al formulario cuando corresponda.
- Utilizar `onCancelar` y `onGuardadoExitoso` para regresar a la pantalla correspondiente.

Atención: los ejemplos iniciales del reparto utilizan identificadores `Int`, pero este módulo utiliza `Long`. Se debe unificar el tipo antes de integrar la navegación y las relaciones de Room.

## 7. Estado de verificación

| Verificación | Estado |
| --- | --- |
| Compilación de los componentes desarrollados | Realizada durante el desarrollo |
| Registro real en Room | Pendiente |
| Consulta y búsqueda con datos reales | Pendiente |
| Edición de registros existentes | Pendiente |
| Eliminación desde la pantalla de detalle | Pendiente de integración con Persona C |
| Navegación entre pantallas | Pendiente de integración con Persona D |
| Inyección con Hilt | Pendiente de integración con Persona D |
| Persistencia de fotografías después de reiniciar | Pendiente de prueba |
| Pruebas offline | Pendiente |

## 8. Observaciones técnicas
- `PetCareDatabase` tiene actualmente una sola entidad y deberá actualizarse para incluir las tablas del módulo de cuidados. Se debe revisar el esquema y las migraciones antes de integrar cambios de la base de datos.
- La carga de fotografías utiliza `OpenDocument` y solicita permisos persistentes de lectura.
- La vista previa de fotografías actualmente utiliza decodificación directa mediante `BitmapFactory`; conviene optimizarla para imágenes grandes.
- El formulario utiliza un `Column` con desplazamiento vertical.
- El formulario distingue entre registro y edición mediante el identificador de la mascota.
- La operación de eliminación está implementada en la capa de datos, pero su interfaz corresponde al módulo de detalle de mascota.

## 9. Pruebas recomendadas después de integrar
1. Registrar una mascota con datos válidos y comprobar que aparezca en Mis Mascotas.
2. Registrar con nombre o especie vacíos y verificar la validación.
3. Introducir un peso inválido y comprobar el mensaje de error.
4. Buscar mascotas por nombre y comprobar que la lista se actualice.
5. Editar una mascota y verificar que sus datos se conserven.
6. Seleccionar una fotografía, cerrar y reabrir la aplicación y comprobar su disponibilidad.
7. Eliminar una mascota y verificar que también se eliminen sus cuidados asociados, cuando la relación esté configurada.
8. Repetir las pruebas sin conexión a internet.

## 10. Estado de entrega
El módulo de mascotas cuenta con sus principales componentes de datos, lógica e interfaz desarrollados.
No se considera completamente integrado ni validado funcionalmente hasta conectar Hilt, navegación, las entidades de cuidados y ejecutar las pruebas correspondientes.
