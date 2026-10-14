package com.am.septimob.petcare.domain.model

enum class Tema { CLARO, OSCURO }

enum class OrdenMascotas { POR_NOMBRE, MAS_RECIENTE }

enum class OrdenHistorial { RECIENTES_PRIMERO, ANTIGUOS_PRIMERO }

data class UserPreferences(
    val tema: Tema = Tema.CLARO,
    val ordenMascotas: OrdenMascotas = OrdenMascotas.POR_NOMBRE,
    val ordenHistorial: OrdenHistorial = OrdenHistorial.RECIENTES_PRIMERO
)
