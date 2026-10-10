package com.am.septimob.petcare.ui.cuidadoform

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuidadoFormScreen(
    uiState: CuidadoFormUiState,
    onTipoChange: (Long) -> Unit,
    onFechaChange: (Long?) -> Unit,
    onNotasChange: (String) -> Unit,
    onGuardar: () -> Unit,
    onEliminar: () -> Unit,
    onCancelar: () -> Unit
) {
    var menuTipoAbierto by remember { mutableStateOf(false) }
    var mostrarCalendario by remember { mutableStateOf(false) }
    var mostrarConfirmacion by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    LaunchedEffect(uiState.fecha) {
        datePickerState.selectedDateMillis = uiState.fecha?.let { it * 86_400_000L }
    }

    val habilitado = !uiState.cargando && !uiState.guardando
    val tipoSeleccionado = uiState.tiposCuidado
        .firstOrNull { it.id == uiState.tipoCuidadoId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = if (uiState.esEdicion) "Editar cuidado" else "Registrar cuidado",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.cargando) {
            CircularProgressIndicator()
            Text("Cargando datos del cuidado...")
        }

        Text("Tipo de cuidado")
        Box {
            OutlinedButton(
                onClick = { menuTipoAbierto = true },
                enabled = habilitado,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(tipoSeleccionado?.nombre ?: "Seleccionar tipo de cuidado")
            }

            DropdownMenu(
                expanded = menuTipoAbierto,
                onDismissRequest = { menuTipoAbierto = false }
            ) {
                uiState.tiposCuidado.forEach { tipo ->
                    DropdownMenuItem(
                        text = { Text(tipo.nombre) },
                        onClick = {
                            onTipoChange(tipo.id)
                            menuTipoAbierto = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Fecha")
        Button(
            onClick = { mostrarCalendario = true },
            enabled = habilitado,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(uiState.fecha?.let { formatearFecha(it) } ?: "Seleccionar fecha")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.notas,
            onValueChange = onNotasChange,
            label = { Text("Notas (opcional)") },
            minLines = 2,
            enabled = habilitado,
            modifier = Modifier.fillMaxWidth()
        )

        uiState.error?.let { mensaje ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = mensaje,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onGuardar,
            enabled = habilitado && !uiState.errorCarga,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = when {
                    uiState.guardando -> "Guardando..."
                    uiState.esEdicion -> "Guardar cambios"
                    else -> "Guardar cuidado"
                }
            )
        }

        if (uiState.esEdicion) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = { mostrarConfirmacion = true },
                enabled = habilitado,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Eliminar cuidado")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onCancelar,
            enabled = !uiState.guardando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancelar")
        }
    }

    if (mostrarCalendario) {
        DatePickerDialog(
            onDismissRequest = {
                datePickerState.selectedDateMillis = uiState.fecha?.let { it * 86_400_000L }
                mostrarCalendario = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { milisegundos ->
                            onFechaChange(Math.floorDiv(milisegundos, 86_400_000L))
                        }
                        mostrarCalendario = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis =
                            uiState.fecha?.let { it * 86_400_000L }
                        mostrarCalendario = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (mostrarConfirmacion) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacion = false },
            title = { Text("Eliminar cuidado") },
            text = { Text("¿Seguro que quieres eliminar este cuidado?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarConfirmacion = false
                        onEliminar()
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmacion = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

private fun formatearFecha(diasDesde1970: Long): String {
    val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    formato.timeZone = TimeZone.getTimeZone("UTC")
    return formato.format(Date(diasDesde1970 * 86_400_000L))
}