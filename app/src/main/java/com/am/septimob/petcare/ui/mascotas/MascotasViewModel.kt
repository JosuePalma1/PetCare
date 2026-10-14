
package com.am.septimob.petcare.ui.mascotas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.am.septimob.petcare.data.repository.MascotaRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MascotasViewModel @Inject constructor(
    private val repository: MascotaRepository
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
                    busquedaActual
                ) { mascotas, busqueda ->
                    MascotasUiState.Success(
                        mascotas = mascotas.filter { mascota ->
                            mascota.nombre.contains(
                                busqueda,
                                ignoreCase = true
                            )
                        },
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
