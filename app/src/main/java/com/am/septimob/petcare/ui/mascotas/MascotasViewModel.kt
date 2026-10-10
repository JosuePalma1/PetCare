
package com.am.septimob.petcare.ui.mascotas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.am.septimob.petcare.data.repository.MascotaRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MascotasViewModel(
    private val repository: MascotaRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<MascotasUiState>(MascotasUiState.Loading)

    val uiState: StateFlow<MascotasUiState> = _uiState

    init {
        observarMascotas()
    }

    private fun observarMascotas() {
        viewModelScope.launch {
            try {
                repository.obtenerMascotas().collect { mascotas ->
                    _uiState.value = MascotasUiState.Success(mascotas)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = MascotasUiState.Error(
                    "No se pudieron cargar las mascotas"
                )
            }
        }
    }
}
