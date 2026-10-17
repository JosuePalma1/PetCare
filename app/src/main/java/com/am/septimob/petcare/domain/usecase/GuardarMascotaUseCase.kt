package com.am.septimob.petcare.domain.usecase

import com.am.septimob.petcare.data.repository.MascotaRepository
import com.am.septimob.petcare.domain.model.Mascota
import javax.inject.Inject

/** Datos tal como los escribe el usuario en el formulario (sin validar todavía). */
data class FormularioMascota(
    val id: Long? = null,
    val nombre: String,
    val especie: String,
    val raza: String = "",
    val fechaNacimiento: Long? = null,
    val sexo: String = "",
    val peso: String = "",
    val fotoUri: String? = null
)

sealed interface ResultadoGuardado {
    data object Guardada : ResultadoGuardado
    data class Invalida(val mensaje: String) : ResultadoGuardado
}

/**
 * Regla de negocio para registrar o editar una mascota: valida los datos, los normaliza
 * (quita espacios, convierte el peso) y solo entonces los guarda en Room.
 * Si `id` es null se inserta; si no, se actualiza.
 */
class GuardarMascotaUseCase @Inject constructor(
    private val mascotaRepository: MascotaRepository
) {
    // hoyEnDias llega como parámetro para que el caso de uso no dependa del reloj (y se pueda probar)
    suspend operator fun invoke(formulario: FormularioMascota, hoyEnDias: Long): ResultadoGuardado {
        validar(formulario, hoyEnDias)?.let { return ResultadoGuardado.Invalida(it) }

        val mascota = Mascota(
            id = formulario.id ?: 0L,
            nombre = formulario.nombre.trim(),
            especie = formulario.especie.trim(),
            raza = formulario.raza.trim(),
            fechaNacimiento = formulario.fechaNacimiento,
            sexo = formulario.sexo,
            pesoKg = formulario.peso.trim().toDoubleOrNull(),
            fotoUri = formulario.fotoUri
        )

        if (formulario.id == null) {
            mascotaRepository.insertarMascota(mascota)
        } else {
            mascotaRepository.actualizarMascota(mascota)
        }
        return ResultadoGuardado.Guardada
    }

    /** Devuelve el mensaje del primer error encontrado, o null si los datos son válidos. */
    private fun validar(formulario: FormularioMascota, hoyEnDias: Long): String? {
        val peso = formulario.peso.trim()
        return when {
            formulario.nombre.isBlank() -> "Debes ingresar el nombre de la mascota"
            formulario.especie.isBlank() -> "Debes seleccionar una especie"
            (formulario.fechaNacimiento ?: 0) > hoyEnDias -> "La fecha de nacimiento no puede ser futura"
            peso.isNotEmpty() && peso.toDoubleOrNull().let { it == null || !it.isFinite() || it <= 0 } ->
                "Ingresa un peso válido mayor que cero"
            else -> null
        }
    }
}
