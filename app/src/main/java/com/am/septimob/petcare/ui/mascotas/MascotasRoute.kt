package com.am.septimob.petcare.ui.mascotas

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun MascotasRoute(
    onAgregarMascota: () -> Unit,
    onMascotaClick: (Long) -> Unit,
    viewModel: MascotasViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val busqueda = when (val estado = uiState) {
        is MascotasUiState.Success -> estado.busqueda
        else -> ""
    }

    MascotasScreen(
        uiState = uiState,
        busqueda = busqueda,
        onBusquedaChange = viewModel::actualizarBusqueda,
        onAgregarMascota = onAgregarMascota,
        onMascotaClick = onMascotaClick
    )
}
