package com.am.septimob.petcare.ui.cuidadoform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun CuidadoFormRoute(
    onCancelar: () -> Unit,
    onListo: () -> Unit,
    viewModel: CuidadoFormViewModel = hiltViewModel()
) {
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