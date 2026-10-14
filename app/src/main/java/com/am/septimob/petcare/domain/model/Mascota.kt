
package com.am.septimob.petcare.domain.model

data class Mascota(
    val id: Long = 0,
    val nombre: String,
    val especie: String,
    val raza: String,
    val fechaNacimiento: Long?,
    val sexo: String,
    val pesoKg: Double?,
    val fotoUri: String? = null
)
