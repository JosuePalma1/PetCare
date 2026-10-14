package com.am.septimob.petcare

import com.am.septimob.petcare.ui.navigation.PetCareRoute
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PetCareRouteTest {

    @Test
    fun mascotaFormRoute_defaultMascotaId_isNull() {
        val route = PetCareRoute.MascotaForm()
        assertNull(route.mascotaId)
    }

    @Test
    fun mascotaFormRoute_withMascotaId_holdsValue() {
        val route = PetCareRoute.MascotaForm(mascotaId = 42L)
        assertEquals(42L, route.mascotaId)
    }

    @Test
    fun detalleMascotaRoute_holdsMascotaId() {
        val route = PetCareRoute.DetalleMascota(mascotaId = 100L)
        assertEquals(100L, route.mascotaId)
    }

    @Test
    fun cuidadoFormRoute_defaultCuidadoId_isNull() {
        val route = PetCareRoute.CuidadoForm(mascotaId = 5L)
        assertEquals(5L, route.mascotaId)
        assertNull(route.cuidadoId)
    }

    @Test
    fun cuidadoFormRoute_withCuidadoId_holdsValues() {
        val route = PetCareRoute.CuidadoForm(mascotaId = 5L, cuidadoId = 12L)
        assertEquals(5L, route.mascotaId)
        assertEquals(12L, route.cuidadoId)
    }
}
