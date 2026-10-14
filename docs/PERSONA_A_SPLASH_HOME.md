# Persona A — Splash, inicio y diseño visual

## Cambios realizados

- Actualicé el Splash con la identidad visual de PetCare, el mensaje de almacenamiento local y un indicador de carga. La navegación espera solo a que se pinte el primer frame; no hay una demora fija.
- Rediseñé Inicio para mostrar el conteo real de mascotas, el estado vacío y el acceso a sus perfiles. El conteo usa `ObtenerCantidadMascotasUseCase` y no datos de ejemplo.
- Conecté la preferencia de tema de DataStore con el tema global, de modo que claro/oscuro se aplique al abrir la app y se conserve al reiniciarla.
- Adapté la navegación: barra inferior en pantallas compactas y rail lateral en pantallas medianas o grandes. Inicio se reorganiza en dos paneles y limita el ancho del contenido en pantallas grandes.
- Actualicé la paleta y el fondo del icono adaptativo al verde azulado de las referencias.
- En “Mis mascotas” añadí foto circular (con icono alternativo si falta o no se puede leer), especie/raza, nacimiento y peso. Se mantuvieron intactos el ViewModel y las operaciones CRUD.

## Acuerdos y alcance

- Se conservan las tres pestañas acordadas por el equipo; el prototipo de referencia muestra cuatro.
- Inicio muestra únicamente resúmenes disponibles con datos reales. El repositorio actual expone cuidados por mascota y el registro parte del detalle de una mascota; no se añadieron cifras ni actividad de cuidados simuladas.

## Validación pendiente

La rama queda lista para la comprobación del equipo. Después de sincronizar Gradle, comprobar compilación y navegación en teléfono y pantalla amplia; verificar también tema persistente, mascotas con/sin foto y campos opcionales de nacimiento/peso. Aún no se declara compilada esta revisión.
