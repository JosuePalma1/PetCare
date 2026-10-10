package com.am.septimob.petcare.ui.detallemascota

import com.am.septimob.petcare.domain.model.Mascota

data class CuidadoItem(
    val id: Long,
    val tipoNombre: String,
    val fecha: Long,
    val notas: String?
)

sealed interface DetalleMascotaUiState {
    data object Loading : DetalleMascotaUiState

    data class Success(
        val mascota: Mascota,
        val cuidados: List<CuidadoItem>
    ) : DetalleMascotaUiState

    data class Error(val mensaje: String) : DetalleMascotaUiState
}