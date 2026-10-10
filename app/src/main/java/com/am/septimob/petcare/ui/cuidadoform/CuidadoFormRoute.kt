package com.am.septimob.petcare.ui.cuidadoform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CuidadoFormRoute(
    viewModel: CuidadoFormViewModel,
    cuidadoId: Long? = null,
    onCancelar: () -> Unit,
    onListo: () -> Unit
) {
    LaunchedEffect(cuidadoId) {
        if (cuidadoId != null) {
            viewModel.cargarCuidado(cuidadoId)
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.listo.collect {
            onListo()
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CuidadoFormScreen(
        uiState = uiState,
        onTipoChange = viewModel::actualizarTipo,
        onFechaChange = viewModel::actualizarFecha,
        onNotasChange = viewModel::actualizarNotas,
        onGuardar = viewModel::guardarCuidado,
        onEliminar = viewModel::eliminarCuidado,
        onCancelar = onCancelar
    )
}