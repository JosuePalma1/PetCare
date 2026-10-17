package com.am.septimob.petcare

import com.am.septimob.petcare.data.repository.MascotaRepository
import com.am.septimob.petcare.domain.model.Mascota
import com.am.septimob.petcare.domain.usecase.FormularioMascota
import com.am.septimob.petcare.domain.usecase.GuardarMascotaUseCase
import com.am.septimob.petcare.domain.usecase.ResultadoGuardado
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GuardarMascotaUseCaseTest {

    private val hoy = 20_000L
    private val repositorio = RepositorioFalso()
    private val guardarMascota = GuardarMascotaUseCase(repositorio)

    private fun formulario(
        id: Long? = null,
        nombre: String = "Luna",
        especie: String = "Gato",
        fechaNacimiento: Long? = null,
        peso: String = ""
    ) = FormularioMascota(
        id = id,
        nombre = nombre,
        especie = especie,
        fechaNacimiento = fechaNacimiento,
        peso = peso
    )

    @Test
    fun nombreVacio_noGuarda() = runBlocking {
        val resultado = guardarMascota(formulario(nombre = "   "), hoy)

        assertEquals(
            ResultadoGuardado.Invalida("Debes ingresar el nombre de la mascota"),
            resultado
        )
        assertTrue(repositorio.insertadas.isEmpty())
    }

    @Test
    fun especieVacia_noGuarda() = runBlocking {
        val resultado = guardarMascota(formulario(especie = ""), hoy)

        assertEquals(ResultadoGuardado.Invalida("Debes seleccionar una especie"), resultado)
        assertTrue(repositorio.insertadas.isEmpty())
    }

    @Test
    fun fechaDeNacimientoFutura_noGuarda() = runBlocking {
        val resultado = guardarMascota(formulario(fechaNacimiento = hoy + 1), hoy)

        assertEquals(
            ResultadoGuardado.Invalida("La fecha de nacimiento no puede ser futura"),
            resultado
        )
        assertTrue(repositorio.insertadas.isEmpty())
    }

    @Test
    fun fechaDeNacimientoHoy_esValida() = runBlocking {
        val resultado = guardarMascota(formulario(fechaNacimiento = hoy), hoy)

        assertEquals(ResultadoGuardado.Guardada, resultado)
    }

    @Test
    fun pesoInvalido_noGuarda() = runBlocking {
        listOf("0", "-3", "abc", "NaN").forEach { peso ->
            val resultado = guardarMascota(formulario(peso = peso), hoy)

            assertEquals(
                "peso '$peso'",
                ResultadoGuardado.Invalida("Ingresa un peso válido mayor que cero"),
                resultado
            )
        }
        assertTrue(repositorio.insertadas.isEmpty())
    }

    @Test
    fun datosValidos_seNormalizanYSeInsertan() = runBlocking {
        val resultado = guardarMascota(
            formulario(nombre = "  Luna ", especie = " Gato", peso = " 4.5 "),
            hoy
        )

        assertEquals(ResultadoGuardado.Guardada, resultado)
        val guardada = repositorio.insertadas.single()
        assertEquals("Luna", guardada.nombre)
        assertEquals("Gato", guardada.especie)
        assertEquals(4.5, guardada.pesoKg!!, 0.0)
        assertTrue(repositorio.actualizadas.isEmpty())
    }

    @Test
    fun pesoVacio_seGuardaComoNulo() = runBlocking {
        guardarMascota(formulario(peso = ""), hoy)

        assertNull(repositorio.insertadas.single().pesoKg)
    }

    @Test
    fun conId_actualizaEnVezDeInsertar() = runBlocking {
        val resultado = guardarMascota(formulario(id = 7L), hoy)

        assertEquals(ResultadoGuardado.Guardada, resultado)
        assertEquals(7L, repositorio.actualizadas.single().id)
        assertTrue(repositorio.insertadas.isEmpty())
    }
}

// Repositorio en memoria: registra lo que el caso de uso intenta guardar, sin Room
private class RepositorioFalso : MascotaRepository {
    val insertadas = mutableListOf<Mascota>()
    val actualizadas = mutableListOf<Mascota>()

    override fun obtenerMascotas(): Flow<List<Mascota>> = flowOf(insertadas)
    override fun obtenerMascotaPorId(id: Long): Flow<Mascota?> = flowOf(null)
    override suspend fun insertarMascota(mascota: Mascota): Long {
        insertadas += mascota
        return insertadas.size.toLong()
    }
    override suspend fun actualizarMascota(mascota: Mascota) {
        actualizadas += mascota
    }
    override suspend fun eliminarMascota(mascota: Mascota) = Unit
}
