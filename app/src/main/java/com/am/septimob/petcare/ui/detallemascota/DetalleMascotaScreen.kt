package com.am.septimob.petcare.ui.detallemascota

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.am.septimob.petcare.domain.model.Mascota
import com.am.septimob.petcare.ui.theme.PetCareTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun DetalleMascotaScreen(
    uiState: DetalleMascotaUiState,
    onEditarMascota: () -> Unit,
    onEliminarMascota: () -> Unit,
    onRegistrarCuidado: () -> Unit,
    onCuidadoClick: (Long) -> Unit,
    onVolver: () -> Unit
) {
    var mostrarConfirmacion by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedButton(onClick = onVolver) {
            Text("Volver")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (uiState) {
            is DetalleMascotaUiState.Loading -> {
                Text("Cargando mascota...")
            }

            is DetalleMascotaUiState.Error -> {
                Text(text = uiState.mensaje)
            }

            is DetalleMascotaUiState.Success -> {
                val mascota = uiState.mascota

                Text(
                    text = mascota.nombre,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text("Especie: ${mascota.especie}")
                Text("Raza: ${mascota.raza}")
                Text("Sexo: ${mascota.sexo}")
                Text(
                    "Nacimiento: " +
                            (mascota.fechaNacimiento?.let { formatearFecha(it) } ?: "Sin registrar")
                )
                Text(
                    "Peso: " +
                            (mascota.pesoKg?.let { "$it kg" } ?: "Sin registrar")
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onEditarMascota,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Editar mascota")
                }

                OutlinedButton(
                    onClick = { mostrarConfirmacion = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Eliminar mascota")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Cuidados",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = onRegistrarCuidado,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Registrar cuidado")
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (uiState.cuidados.isEmpty()) {
                    Text("Aún no hay cuidados registrados")
                } else {
                    LazyColumn {
                        items(
                            items = uiState.cuidados,
                            key = { cuidado -> cuidado.id }
                        ) { cuidado ->
                            Card(
                                onClick = { onCuidadoClick(cuidado.id) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 2.dp
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = cuidado.tipoNombre,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(formatearFecha(cuidado.fecha))
                                    cuidado.notas?.takeIf { it.isNotBlank() }?.let {
                                        Text(it)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarConfirmacion) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacion = false },
            title = { Text("Eliminar mascota") },
            text = {
                Text("Se eliminará la mascota y todos sus cuidados. ¿Continuar?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarConfirmacion = false
                        onEliminarMascota()
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

@Preview(showBackground = true)
@Composable
private fun DetalleMascotaScreenPreview() {
    PetCareTheme {
        DetalleMascotaScreen(
            uiState = DetalleMascotaUiState.Success(
                mascota = Mascota(
                    id = 1, nombre = "Luna", especie = "Perro", raza = "Criollo",
                    fechaNacimiento = 19000, sexo = "Hembra", pesoKg = 12.5
                ),
                cuidados = listOf(
                    CuidadoItem(1, "Vacunación", 20000, "Primera dosis")
                )
            ),
            onEditarMascota = {}, onEliminarMascota = {}, onRegistrarCuidado = {},
            onCuidadoClick = {}, onVolver = {}
        )
    }
}