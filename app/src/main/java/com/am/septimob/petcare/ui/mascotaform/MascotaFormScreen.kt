package com.am.septimob.petcare.ui.mascotaform

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.am.septimob.petcare.ui.components.rememberFotoMascota

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MascotaFormScreen(
    uiState: MascotaFormUiState,
    onNombreChange: (String) -> Unit,
    onEspecieChange: (String) -> Unit,
    onRazaChange: (String) -> Unit,
    onSexoChange: (String) -> Unit,
    onPesoChange: (String) -> Unit,
    onFechaNacimientoChange: (Long?) -> Unit,
    onFotoChange: (String?) -> Unit,
    onGuardarMascota: () -> Unit,
    onCancelar: () -> Unit
) {
    var mostrarCalendario by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var fechaConfirmadaMillis by remember { mutableStateOf<Long?>(null) }
    var fechaNacimiento by remember { mutableStateOf("") }

    val context = LocalContext.current

    val selectorFoto = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                onFotoChange(uri.toString())
            } catch (e: SecurityException) {
                // No actualizamos la foto si no podemos
                // conservar el permiso de lectura.
            }
        }
    }

    val fotoBitmap = rememberFotoMascota(uiState.fotoUri, tamano = 120.dp)

    LaunchedEffect(uiState.fechaNacimiento) {
        fechaNacimiento = uiState.fechaNacimiento?.let { dias ->
            val millis = dias * 86_400_000L
            val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            formato.timeZone = java.util.TimeZone.getTimeZone("UTC")
            formato.format(Date(millis))
        } ?: ""

        val milisegundos = uiState.fechaNacimiento?.let { dias ->
            dias * 86_400_000L
        }

        fechaConfirmadaMillis = milisegundos
        datePickerState.selectedDateMillis = milisegundos
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = if (uiState.esEdicion) {
                "Editar mascota"
            } else {
                "Registrar mascota"
            }
        )

        if (uiState.cargando) {
            CircularProgressIndicator()

            Text(
                text = "Cargando datos de la mascota..."
            )
        }

        OutlinedButton(
            onClick = {
                selectorFoto.launch(arrayOf("image/*"))
            },
            enabled = !uiState.cargando && !uiState.guardando
        ) {
            Text("Agregar foto")
        }

        if (fotoBitmap != null) {
            Image(
                bitmap = fotoBitmap,
                contentDescription = "Fotografía de la mascota",
                modifier = Modifier.size(120.dp),
                contentScale = ContentScale.Crop
            )
        }

        OutlinedTextField(
            value = uiState.nombre,
            onValueChange = onNombreChange,
            label = { Text("Nombre de la mascota") },
            singleLine = true,
            enabled = !uiState.cargando && !uiState.guardando
        )

        OutlinedTextField(
            value = uiState.especie,
            onValueChange = onEspecieChange,
            label = { Text("Especie") },
            singleLine = true,
            enabled = !uiState.cargando && !uiState.guardando
        )

        OutlinedTextField(
            value = uiState.raza,
            onValueChange = onRazaChange,
            label = { Text("Raza") },
            singleLine = true,
            enabled = !uiState.cargando && !uiState.guardando
        )

        Button(
            onClick = { mostrarCalendario = true },
            enabled = !uiState.cargando && !uiState.guardando
        ) {
            Text(
                text = if (fechaNacimiento.isEmpty()) {
                    "Seleccionar fecha de nacimiento"
                } else {
                    fechaNacimiento
                }
            )
        }

        Text(text = "Sexo")

        Row {
            RadioButton(
                selected = uiState.sexo == "Macho",
                onClick = { onSexoChange("Macho") },
                enabled = !uiState.cargando && !uiState.guardando
            )
            Text(text = "Macho")

            RadioButton(
                selected = uiState.sexo == "Hembra",
                onClick = { onSexoChange("Hembra") },
                enabled = !uiState.cargando && !uiState.guardando
            )
            Text(text = "Hembra")
        }

        OutlinedTextField(
            value = uiState.peso,
            onValueChange = onPesoChange,
            label = { Text("Peso (kg)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            enabled = !uiState.cargando && !uiState.guardando
        )

        uiState.error?.let { mensaje ->
            Row {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Advertencia",
                    tint = MaterialTheme.colorScheme.error
                )

                Text(
                    text = mensaje,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onGuardarMascota,
            enabled = !uiState.guardando &&
                      !uiState.cargando &&
                      !uiState.errorCarga,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (uiState.guardando) {
                    "Guardando..."
                } else if (uiState.esEdicion) {
                    "Guardar cambios"
                } else {
                    "Guardar mascota"
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onCancelar,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.guardando
        ) {
            Text("Cancelar")
        }
    }

    if (mostrarCalendario) {
        DatePickerDialog(
            onDismissRequest = {
                datePickerState.selectedDateMillis = fechaConfirmadaMillis
                mostrarCalendario = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { milisegundos ->
                            fechaConfirmadaMillis = milisegundos
                            val diasDesde1970 = Math.floorDiv(
                                milisegundos,
                                86_400_000L
                            )
                            onFechaNacimientoChange(diasDesde1970)

                            val formato = SimpleDateFormat(
                                "dd/MM/yyyy",
                                Locale.getDefault()
                            )
                            formato.timeZone = java.util.TimeZone.getTimeZone("UTC")
                            fechaNacimiento = formato.format(Date(milisegundos))
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
                        datePickerState.selectedDateMillis = fechaConfirmadaMillis
                        mostrarCalendario = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(
                state = datePickerState
            )
        }
    }
}
