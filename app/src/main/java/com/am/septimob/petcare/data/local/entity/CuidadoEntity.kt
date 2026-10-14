package com.am.septimob.petcare.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cuidados",
    foreignKeys = [
        ForeignKey(
            entity = MascotaEntity::class,
            parentColumns = ["id"],
            childColumns = ["mascotaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TipoCuidadoEntity::class,
            parentColumns = ["id"],
            childColumns = ["tipoCuidadoId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("mascotaId"), Index("tipoCuidadoId")]
)
data class CuidadoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mascotaId: Long,
    val tipoCuidadoId: Long,
    val fecha: Long,
    val notas: String? = null
)