package com.am.septimob.petcare.ui.detallemascota

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun DetalleMascotaRoute(
    viewModel: DetalleMascotaViewModel,
    onEditarMascota: () -> Unit,
    onRegistrarCuidado: () -> Unit,
    onCuidadoClick: (Long) -> Unit,
    onMascotaEliminada: () -> Unit,
    onVolver: () -> Unit
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