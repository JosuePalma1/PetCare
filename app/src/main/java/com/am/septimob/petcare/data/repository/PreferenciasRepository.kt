package com.am.septimob.petcare.data.repository

import com.am.septimob.petcare.domain.model.OrdenHistorial
import com.am.septimob.petcare.domain.model.OrdenMascotas
import com.am.septimob.petcare.domain.model.Tema
import com.am.septimob.petcare.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface PreferenciasRepository {

    val preferencias: Flow<UserPreferences>

    suspend fun actualizarTema(tema: Tema)

    suspend fun actualizarOrdenMascotas(orden: OrdenMascotas)

    suspend fun actualizarOrdenHistorial(orden: OrdenHistorial)
}
