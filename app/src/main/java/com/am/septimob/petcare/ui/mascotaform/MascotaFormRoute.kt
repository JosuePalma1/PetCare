package com.am.septimob.petcare.ui.mascotaform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun MascotaFormRoute(
    viewModel: MascotaFormViewModel,
    mascotaId: Long? = null,
    onCancelar: () -> Unit,
    onGuardadoExitoso: () -> Unit
) {
    LaunchedEffect(mascotaId) {
        if (mascotaId != null) {
            viewModel.cargarMascota(mascotaId)
        }
    }

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
