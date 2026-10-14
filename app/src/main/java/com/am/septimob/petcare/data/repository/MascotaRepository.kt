
package com.am.septimob.petcare.data.repository

import com.am.septimob.petcare.domain.model.Mascota
import kotlinx.coroutines.flow.Flow

interface MascotaRepository {

    fun obtenerMascotas(): Flow<List<Mascota>>

    fun obtenerMascotaPorId(id: Long): Flow<Mascota?>

    suspend fun insertarMascota(mascota: Mascota): Long

    suspend fun actualizarMascota(mascota: Mascota)

    suspend fun eliminarMascota(mascota: Mascota)
}
