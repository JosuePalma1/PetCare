package com.am.septimob.petcare.data.repository

import com.am.septimob.petcare.domain.model.Cuidado
import com.am.septimob.petcare.domain.model.TipoCuidado
import kotlinx.coroutines.flow.Flow

interface CuidadoRepository {
    fun obtenerCuidadosDeMascota(mascotaId: Long): Flow<List<Cuidado>>
    suspend fun obtenerCuidadoPorId(id: Long): Cuidado?
    suspend fun insertarCuidado(cuidado: Cuidado): Long
    suspend fun actualizarCuidado(cuidado: Cuidado)
    suspend fun eliminarCuidado(cuidado: Cuidado)
    fun obtenerTiposCuidado(): Flow<List<TipoCuidado>>
}