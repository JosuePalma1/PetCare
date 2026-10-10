package com.am.septimob.petcare.ui.mascotas

import com.am.septimob.petcare.domain.model.Mascota

sealed interface MascotasUiState {

    data object Loading : MascotasUiState

    data class Success(
        val mascotas: List<Mascota>
    ) : MascotasUiState

    data class Error(
        val mensaje: String
    ) : MascotasUiState
}
