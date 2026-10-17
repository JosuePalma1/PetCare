package com.am.septimob.petcare.ui.home

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Success(
        val totalMascotas: Int,
        val totalCuidados: Int,
        val cuidadosRecientes: List<CuidadoReciente>
    ) : HomeUiState

    data class Error(val mensaje: String) : HomeUiState
}

data class CuidadoReciente(
    val id: Long,
    val mascotaId: Long,
    val nombreMascota: String,
    val nombreTipo: String,
    val fecha: Long,
    val notas: String?
)
