# Persona A — Splash, inicio y diseño visual

**Responsable:** Marcelo Matias Nieto Medina (Persona A)

## Cambios realizados

- Actualicé el Splash con la identidad visual de PetCare, el mensaje de almacenamiento local y un indicador de carga. Tras pintar el primer frame se mantiene 800 ms y luego abre Inicio sin dejar el Splash en la pila.
- Rediseñé Inicio con datos reales: total de mascotas (vía `ObtenerCantidadMascotasUseCase`), total de cuidados, los 2 cuidados más recientes y el estado vacío. Sin mascotas, el botón abre directamente el formulario de registro; con mascotas, lleva a la lista.
- Conecté la preferencia de tema de DataStore con el tema global, de modo que claro/oscuro se aplique al abrir la app y se conserve al reiniciarla.
- Adapté la navegación: barra inferior en pantallas compactas y rail lateral en pantallas medianas o grandes. En pantallas medianas o grandes, Inicio y Mis mascotas amplían los márgenes e Inicio limita el ancho del contenido a 1040 dp.
- Actualicé la paleta y el fondo del icono adaptativo al verde azulado de las referencias.
- En “Mis mascotas” añadí foto circular (con icono alternativo si falta o no se puede leer), especie/raza, nacimiento y peso. Se mantuvieron intactos el ViewModel y las operaciones CRUD.

## Acuerdos y alcance

- Se conservan las tres pestañas acordadas por el equipo; el prototipo de referencia muestra cuatro.
- Inicio muestra únicamente datos reales; no hay cifras ni actividad simuladas. Como el repositorio expone cuidados por mascota, la actividad reciente combina los cuidados de todas las mascotas.

## Validación pendiente

La rama queda lista para la comprobación del equipo. Después de sincronizar Gradle, comprobar compilación y navegación en teléfono y pantalla amplia; verificar también tema persistente, mascotas con/sin foto y campos opcionales de nacimiento/peso. Aún no se declara compilada esta revisión.
