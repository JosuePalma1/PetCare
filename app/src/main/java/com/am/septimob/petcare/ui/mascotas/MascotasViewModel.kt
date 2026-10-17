
package com.am.septimob.petcare.ui.mascotas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.am.septimob.petcare.data.repository.MascotaRepository
import com.am.septimob.petcare.data.repository.PreferenciasRepository
import com.am.septimob.petcare.domain.model.Mascota
import com.am.septimob.petcare.domain.model.OrdenMascotas
import java.text.Collator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MascotasViewModel @Inject constructor(
    private val repository: MascotaRepository,
    private val preferenciasRepository: PreferenciasRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<MascotasUiState>(MascotasUiState.Loading)

    val uiState: StateFlow<MascotasUiState> = _uiState

    private val busquedaActual = MutableStateFlow("")

    init {
        observarMascotas()
    }

    private fun observarMascotas() {
        viewModelScope.launch {
            try {
                combine(
                    repository.obtenerMascotas(),
                    busquedaActual,
                    preferenciasRepository.preferencias
                ) { mascotas, busqueda, preferencias ->
                    // Sin trim, buscar " " no coincidía con nada y la pantalla lo trataba como lista vacía
                    val texto = busqueda.trim()
                    MascotasUiState.Success(
                        mascotas = mascotas
                            .filter { mascota ->
                                mascota.nombre.contains(texto, ignoreCase = true)
                            }
                            .ordenadas(preferencias.ordenMascotas),
                        busqueda = busqueda
                    )
                }.collect { estado ->
                    _uiState.value = estado
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.value = MascotasUiState.Error(
                    "No se pudieron cargar las mascotas"
                )
            }
        }
    }

    fun actualizarBusqueda(texto: String) {
        busquedaActual.value = texto
    }
}

// Collator: "abby" va antes que "Max" y "Ámbar" junto a "Ana" (el ORDER BY de SQLite distingue mayúsculas)
private fun List<Mascota>.ordenadas(orden: OrdenMascotas): List<Mascota> = when (orden) {
    OrdenMascotas.POR_NOMBRE -> sortedWith(compareBy(Collator.getInstance()) { it.nombre })
    // El id es autoincremental: el mayor es el último registrado
    OrdenMascotas.MAS_RECIENTE -> sortedByDescending { it.id }
}
