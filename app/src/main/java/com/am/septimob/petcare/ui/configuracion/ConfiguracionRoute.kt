package com.am.septimob.petcare.ui.configuracion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ConfiguracionRoute(
    viewModel: ConfiguracionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ConfiguracionScreen(
        uiState = uiState,
        onTemaChange = viewModel::cambiarTema,
        onOrdenMascotasChange = viewModel::cambiarOrdenMascotas,
        onOrdenHistorialChange = viewModel::cambiarOrdenHistorial
    )
}
