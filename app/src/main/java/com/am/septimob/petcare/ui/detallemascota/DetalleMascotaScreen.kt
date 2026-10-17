package com.am.septimob.petcare.ui.detallemascota

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.am.septimob.petcare.domain.model.Mascota
import com.am.septimob.petcare.ui.theme.PetCareTheme
import com.am.septimob.petcare.ui.components.rememberFotoMascota
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
    var menuAbierto by remember { mutableStateOf(false) }

    val titulo = (uiState as? DetalleMascotaUiState.Success)?.mascota?.nombre ?: "Detalle"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onVolver) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver"
                )
            }
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            if (uiState is DetalleMascotaUiState.Success) {
                Box {
                    IconButton(onClick = { menuAbierto = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Más opciones"
                        )
                    }
                    DropdownMenu(
                        expanded = menuAbierto,
                        onDismissRequest = { menuAbierto = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Eliminar mascota") },
                            onClick = {
                                menuAbierto = false
                                mostrarConfirmacion = true
                            }
                        )
                    }
                }
            }
        }

        when (uiState) {
            is DetalleMascotaUiState.Loading -> {
                Text("Cargando mascota...")
            }

            is DetalleMascotaUiState.Error -> {
                Text(text = uiState.mensaje)
            }

            is DetalleMascotaUiState.Success -> {
                val mascota = uiState.mascota

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        FotoMascota(mascota.fotoUri, mascota.nombre)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = mascota.nombre,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = listOf(mascota.especie, mascota.raza)
                                .filter { it.isNotBlank() }
                                .joinToString(" • "),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                                    .copy(alpha = 0.4f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Información",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                FilaInfo(
                                    "Nacimiento",
                                    mascota.fechaNacimiento?.let { formatearFecha(it) }
                                        ?: "Sin registrar"
                                )
                                HorizontalDivider()
                                FilaInfo(
                                    "Sexo",
                                    mascota.sexo.ifBlank { "Sin registrar" }
                                )
                                HorizontalDivider()
                                FilaInfo(
                                    "Peso",
                                    mascota.pesoKg?.let { "$it kg" } ?: "Sin registrar"
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        Button(
                            onClick = onRegistrarCuidado,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Registrar cuidado")
                        }
                        TextButton(onClick = onEditarMascota) {
                            Text("Editar mascota")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    item {
                        Text(
                            text = "Cuidados",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (uiState.cuidados.isEmpty()) {
                        item {
                            Text("Aún no hay cuidados registrados")
                        }
                    } else {
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
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.DateRange,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(formatearFecha(cuidado.fecha))
                                    }
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
private fun FilaInfo(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = etiqueta,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = valor,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun FotoMascota(fotoUri: String?, nombre: String) {
    val bitmap = rememberFotoMascota(fotoUri, tamano = 160.dp)

    Box(
        modifier = Modifier
            .size(160.dp)
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
                style = MaterialTheme.typography.displayMedium
            )
        }
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