package com.am.septimob.petcare.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tipos_cuidado")
data class TipoCuidadoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String
)