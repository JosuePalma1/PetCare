package com.am.septimob.petcare.ui.navigation

import kotlinx.serialization.Serializable

sealed interface PetCareRoute {

    @Serializable
    data object Splash : PetCareRoute

    @Serializable
    data object Home : PetCareRoute

    @Serializable
    data object Mascotas : PetCareRoute

    @Serializable
    data object Configuracion : PetCareRoute

    @Serializable
    data class DetalleMascota(val mascotaId: Long) : PetCareRoute

    // mascotaId = null → registrar; con valor → editar
    @Serializable
    data class MascotaForm(val mascotaId: Long? = null) : PetCareRoute

    // cuidadoId = null → registrar; con valor → editar
    @Serializable
    data class CuidadoForm(val mascotaId: Long, val cuidadoId: Long? = null) : PetCareRoute
}
