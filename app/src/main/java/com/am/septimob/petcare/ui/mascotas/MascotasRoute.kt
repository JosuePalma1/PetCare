package com.am.septimob.petcare.ui.mascotas

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue

@Composable
fun MascotasRoute(
    viewModel: MascotasViewModel,
    onAgregarMascota: () -> Unit,
    onMascotaClick: (Long) -> Unit
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
