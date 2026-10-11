package com.am.septimob.petcare.ui.configuracion

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.am.septimob.petcare.domain.model.OrdenHistorial
import com.am.septimob.petcare.domain.model.OrdenMascotas
import com.am.septimob.petcare.domain.model.Tema

@Composable
fun ConfiguracionScreen(
    uiState: ConfiguracionUiState,
    onTemaChange: (Tema) -> Unit,
    onOrdenMascotasChange: (OrdenMascotas) -> Unit,
    onOrdenHistorialChange: (OrdenHistorial) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Configuración",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (uiState) {
            is ConfiguracionUiState.Loading -> {
                Text("Cargando preferencias...")
            }

            is ConfiguracionUiState.Success -> {
                val preferencias = uiState.preferencias

                SeccionOpciones(
                    titulo = "Tema",
                    opciones = Tema.entries,
                    seleccionada = preferencias.tema,
                    etiqueta = { tema ->
                        when (tema) {
                            Tema.CLARO -> "Claro"
                            Tema.OSCURO -> "Oscuro"
                        }
                    },
                    onSeleccionar = onTemaChange
                )

                SeccionOpciones(
                    titulo = "Ordenar mascotas",
                    opciones = OrdenMascotas.entries,
                    seleccionada = preferencias.ordenMascotas,
                    etiqueta = { orden ->
                        when (orden) {
                            OrdenMascotas.POR_NOMBRE -> "Por nombre (A-Z)"
                            OrdenMascotas.MAS_RECIENTE -> "Más recientes primero"
                        }
                    },
                    onSeleccionar = onOrdenMascotasChange
                )

                SeccionOpciones(
                    titulo = "Ordenar historial de cuidados",
                    opciones = OrdenHistorial.entries,
                    seleccionada = preferencias.ordenHistorial,
                    etiqueta = { orden ->
                        when (orden) {
                            OrdenHistorial.RECIENTES_PRIMERO -> "Más recientes primero"
                            OrdenHistorial.ANTIGUOS_PRIMERO -> "Más antiguos primero"
                        }
                    },
                    onSeleccionar = onOrdenHistorialChange
                )
            }
        }
    }
}

// Tarjeta con un grupo de RadioButton: toda la fila es pulsable, no solo el círculo
@Composable
private fun <T> SeccionOpciones(
    titulo: String,
    opciones: List<T>,
    seleccionada: T,
    etiqueta: (T) -> String,
    onSeleccionar: (T) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .selectableGroup()
        ) {
            Text(
                text = titulo,
                fontWeight = FontWeight.Bold
            )

            opciones.forEach { opcion ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = opcion == seleccionada,
                            onClick = { onSeleccionar(opcion) },
                            role = Role.RadioButton
                        )
                        .padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = opcion == seleccionada,
                        onClick = null
                    )
                    Text(
                        text = etiqueta(opcion),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}
