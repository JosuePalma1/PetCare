package com.am.septimob.petcare.ui.mascotaform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun MascotaFormRoute(
    onCancelar: () -> Unit,
    onGuardadoExitoso: () -> Unit,
    viewModel: MascotaFormViewModel = hiltViewModel()
) {
    LaunchedEffect(viewModel) {
        viewModel.guardadoExitoso.collect {
            onGuardadoExitoso()
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MascotaFormScreen(
        uiState = uiState,
        onNombreChange = viewModel::actualizarNombre,
        onEspecieChange = viewModel::actualizarEspecie,
        onRazaChange = viewModel::actualizarRaza,
        onSexoChange = viewModel::actualizarSexo,
        onPesoChange = viewModel::actualizarPeso,
        onFechaNacimientoChange = viewModel::actualizarFechaNacimiento,
        onFotoChange = viewModel::actualizarFotoUri,
        onGuardarMascota = viewModel::guardarMascota,
        onCancelar = onCancelar
    )
}
