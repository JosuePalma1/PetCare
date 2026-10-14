package com.am.septimob.petcare.domain.model

data class Cuidado(
    val id: Long = 0,
    val mascotaId: Long,
    val tipoCuidadoId: Long,
    val fecha: Long,
    val notas: String? = null
)