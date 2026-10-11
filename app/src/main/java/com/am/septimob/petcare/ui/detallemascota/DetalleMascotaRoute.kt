package com.am.septimob.petcare.ui.detallemascota

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun DetalleMascotaRoute(
    onEditarMascota: () -> Unit,
    onRegistrarCuidado: () -> Unit,
    onCuidadoClick: (Long) -> Unit,
    onMascotaEliminada: () -> Unit,
    onVolver: () -> Unit,
    viewModel: DetalleMascotaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.eventos.collect { evento ->
            when (evento) {
                DetalleMascotaEvento.MascotaEliminada -> onMascotaEliminada()
                is DetalleMascotaEvento.Error ->
                    Toast.makeText(context, evento.mensaje, Toast.LENGTH_SHORT).show()
            }
        }
    }

    DetalleMascotaScreen(
        uiState = uiState,
        onEditarMascota = onEditarMascota,
        onEliminarMascota = viewModel::eliminarMascota,
        onRegistrarCuidado = onRegistrarCuidado,
        onCuidadoClick = onCuidadoClick,
        onVolver = onVolver
    )
}