package com.am.septimob.petcare.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.am.septimob.petcare.domain.usecase.ObtenerCantidadMascotasUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class HomeViewModel @Inject constructor(
    obtenerCantidadMascotas: ObtenerCantidadMascotasUseCase
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = obtenerCantidadMascotas()
        .map { total -> HomeUiState.Success(total) as HomeUiState }
        .catch { emit(HomeUiState.Error("No se pudo cargar el resumen")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState.Loading
        )
}
