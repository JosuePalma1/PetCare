package com.am.septimob.petcare.ui.mascotas

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MascotasScreen(
    uiState: MascotasUiState,
    busqueda: String,
    onBusquedaChange: (String) -> Unit,
    onAgregarMascota: () -> Unit,
    onMascotaClick: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Mis mascotas",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = busqueda,
            onValueChange = onBusquedaChange,
            label = { Text("Buscar mascota") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onAgregarMascota,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Agregar mascota")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (uiState) {
            is MascotasUiState.Loading -> {
                Text("Cargando mascotas...")
            }

            is MascotasUiState.Success -> {
                if (uiState.mascotas.isEmpty()) {
                    Text("No hay mascotas para mostrar")
                } else {
                    LazyColumn {
                        items(
                            items = uiState.mascotas,
                            key = { mascota -> mascota.id }
                        ) { mascota ->
                            Card(
                                onClick = {
                                    onMascotaClick(mascota.id)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 2.dp
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = mascota.nombre,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Text(
                                        text = "${mascota.especie} · ${mascota.raza}"
                                    )
                                }
                            }
                        }
                    }
                }
            }

            is MascotasUiState.Error -> {
                Text(text = uiState.mensaje)
            }
        }
    }
}
