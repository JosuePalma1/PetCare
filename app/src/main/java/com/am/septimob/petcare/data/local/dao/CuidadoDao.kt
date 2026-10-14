package com.am.septimob.petcare.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.am.septimob.petcare.data.local.entity.CuidadoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CuidadoDao {
    @Query("SELECT * FROM cuidados WHERE mascotaId = :mascotaId ORDER BY fecha DESC")
    fun observarPorMascota(mascotaId: Long): Flow<List<CuidadoEntity>>

    @Query("SELECT * FROM cuidados WHERE id = :id")
    suspend fun obtenerPorId(id: Long): CuidadoEntity?

    @Insert
    suspend fun insertar(cuidado: CuidadoEntity): Long

    @Update
    suspend fun actualizar(cuidado: CuidadoEntity)

    @Delete
    suspend fun eliminar(cuidado: CuidadoEntity)
}