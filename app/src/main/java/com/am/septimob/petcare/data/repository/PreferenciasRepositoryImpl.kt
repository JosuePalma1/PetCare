package com.am.septimob.petcare.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.am.septimob.petcare.domain.model.OrdenHistorial
import com.am.septimob.petcare.domain.model.OrdenMascotas
import com.am.septimob.petcare.domain.model.Tema
import com.am.septimob.petcare.domain.model.UserPreferences
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferenciasRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : PreferenciasRepository {

    private object Claves {
        val TEMA = stringPreferencesKey("tema")
        val ORDEN_MASCOTAS = stringPreferencesKey("orden_mascotas")
        val ORDEN_HISTORIAL = stringPreferencesKey("orden_historial")
    }

    private val valoresPorDefecto = UserPreferences()

    override val preferencias: Flow<UserPreferences> = dataStore.data.map { prefs ->
        UserPreferences(
            tema = prefs[Claves.TEMA].toEnumOr(valoresPorDefecto.tema),
            ordenMascotas = prefs[Claves.ORDEN_MASCOTAS].toEnumOr(valoresPorDefecto.ordenMascotas),
            ordenHistorial = prefs[Claves.ORDEN_HISTORIAL].toEnumOr(valoresPorDefecto.ordenHistorial)
        )
    }

    override suspend fun actualizarTema(tema: Tema) {
        dataStore.edit { it[Claves.TEMA] = tema.name }
    }

    override suspend fun actualizarOrdenMascotas(orden: OrdenMascotas) {
        dataStore.edit { it[Claves.ORDEN_MASCOTAS] = orden.name }
    }

    override suspend fun actualizarOrdenHistorial(orden: OrdenHistorial) {
        dataStore.edit { it[Claves.ORDEN_HISTORIAL] = orden.name }
    }
}

// Si el valor guardado no existe o ya no coincide con ninguna opción, se usa el valor por defecto
private inline fun <reified T : Enum<T>> String?.toEnumOr(defecto: T): T =
    enumValues<T>().firstOrNull { it.name == this } ?: defecto
