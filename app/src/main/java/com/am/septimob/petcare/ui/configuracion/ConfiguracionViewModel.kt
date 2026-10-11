package com.am.septimob.petcare.ui.configuracion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.am.septimob.petcare.data.repository.PreferenciasRepository
import com.am.septimob.petcare.domain.model.OrdenHistorial
import com.am.septimob.petcare.domain.model.OrdenMascotas
import com.am.septimob.petcare.domain.model.Tema
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ConfiguracionViewModel @Inject constructor(
    private val repository: PreferenciasRepository
) : ViewModel() {

    // DataStore es la única fuente de verdad: la pantalla solo refleja lo guardado
    val uiState: StateFlow<ConfiguracionUiState> = repository.preferencias
        .map<_, ConfiguracionUiState> { ConfiguracionUiState.Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ConfiguracionUiState.Loading
        )

    fun cambiarTema(tema: Tema) {
        viewModelScope.launch { repository.actualizarTema(tema) }
    }

    fun cambiarOrdenMascotas(orden: OrdenMascotas) {
        viewModelScope.launch { repository.actualizarOrdenMascotas(orden) }
    }

    fun cambiarOrdenHistorial(orden: OrdenHistorial) {
        viewModelScope.launch { repository.actualizarOrdenHistorial(orden) }
    }
}
