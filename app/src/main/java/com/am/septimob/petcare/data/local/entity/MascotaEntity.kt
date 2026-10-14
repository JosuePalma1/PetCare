
package com.am.septimob.petcare.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mascotas")
data class MascotaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val nombre: String,
    val especie: String,
    val raza: String,
    val fechaNacimiento: Long?,
    val sexo: String,
    val pesoKg: Double?,
    val fotoUri: String? = null
)
