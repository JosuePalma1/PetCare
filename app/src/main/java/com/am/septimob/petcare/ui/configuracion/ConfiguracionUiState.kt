package com.am.septimob.petcare.ui.configuracion

import com.am.septimob.petcare.domain.model.UserPreferences

sealed interface ConfiguracionUiState {

    data object Loading : ConfiguracionUiState

    data class Success(
        val preferencias: UserPreferences
    ) : ConfiguracionUiState
}
