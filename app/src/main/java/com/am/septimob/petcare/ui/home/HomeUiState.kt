package com.am.septimob.petcare.ui.home

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val totalMascotas: Int) : HomeUiState
    data class Error(val mensaje: String) : HomeUiState
}
