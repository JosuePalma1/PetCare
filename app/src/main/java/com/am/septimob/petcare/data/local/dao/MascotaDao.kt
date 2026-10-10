
package com.am.septimob.petcare.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.am.septimob.petcare.data.local.entity.MascotaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MascotaDao {

    @Query("SELECT * FROM mascotas ORDER BY nombre ASC")
    fun obtenerMascotas(): Flow<List<MascotaEntity>>

    @Query("SELECT * FROM mascotas WHERE id = :id")
    fun obtenerMascotaPorId(id: Long): Flow<MascotaEntity?>

    @Insert
    suspend fun insertarMascota(mascota: MascotaEntity): Long

    @Update
    suspend fun actualizarMascota(mascota: MascotaEntity)

    @Delete
    suspend fun eliminarMascota(mascota: MascotaEntity)
}
