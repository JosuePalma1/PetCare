package com.am.septimob.petcare.ui.cuidadoform

import com.am.septimob.petcare.domain.model.TipoCuidado

data class CuidadoFormUiState(
    val id: Long? = null,
    val mascotaNombre: String = "",
    val mascotaFotoUri: String? = null,
    val tipoCuidadoId: Long? = null,
    val fecha: Long? = null,
    val notas: String = "",
    val tiposCuidado: List<TipoCuidado> = emptyList(),
    val cargando: Boolean = false,
    val guardando: Boolean = false,
    val errorCarga: Boolean = false,
    val error: String? = null
) {
    val esEdicion: Boolean
        get() = id != null
}