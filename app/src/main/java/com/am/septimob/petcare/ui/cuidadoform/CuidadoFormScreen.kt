package com.am.septimob.petcare.ui.cuidadoform

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onCancelar, enabled = !uiState.guardando) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver"
                )
            }
            Text(
                text = if (uiState.esEdicion) "Editar cuidado" else "Registrar cuidado",
                style = MaterialTheme.typography.titleLarge
            )
        }

        if (uiState.cargando) {
            Spacer(modifier = Modifier.height(8.dp))
            CircularProgressIndicator()
            Text("Cargando datos del cuidado...")
        }

        Etiqueta("Mascota")
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
                    .copy(alpha = 0.4f)
            )
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvatarMascota(uiState.mascotaFotoUri, uiState.mascotaNombre)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = uiState.mascotaNombre.ifBlank { "Mascota" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Etiqueta("Tipo de cuidado")
        Box {
            OutlinedButton(
                onClick = { menuTipoAbierto = true },
                enabled = habilitado,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = tipoSeleccionado?.nombre ?: "Seleccionar tipo de cuidado",
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
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

        Etiqueta("Fecha")
        OutlinedButton(
            onClick = { mostrarCalendario = true },
            enabled = habilitado,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Default.DateRange, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = uiState.fecha?.let { formatearFecha(it) } ?: "Seleccionar fecha",
                modifier = Modifier.weight(1f)
            )
        }

        Etiqueta("Notas")
        OutlinedTextField(
            value = uiState.notas,
            onValueChange = onNotasChange,
            placeholder = { Text("Opcional") },
            minLines = 4,
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

        Spacer(modifier = Modifier.height(24.dp))

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
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
            ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Eliminar cuidado")
            }
        }

        TextButton(
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
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
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

@Composable
private fun Etiqueta(texto: String) {
    Text(
        text = texto,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
    )
}

@Composable
private fun AvatarMascota(fotoUri: String?, nombre: String) {
    val context = LocalContext.current
    val bitmap = remember(fotoUri) {
        fotoUri?.let { direccion ->
            try {
                context.contentResolver.openInputStream(Uri.parse(direccion))
                    ?.use { archivo ->
                        BitmapFactory.decodeStream(archivo)?.asImageBitmap()
                    }
            } catch (e: Exception) {
                null
            }
        }
    }

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = "Fotografía de la mascota",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = nombre.take(1).uppercase(),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

private fun formatearFecha(diasDesde1970: Long): String {
    val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    formato.timeZone = TimeZone.getTimeZone("UTC")
    return formato.format(Date(diasDesde1970 * 86_400_000L))
}