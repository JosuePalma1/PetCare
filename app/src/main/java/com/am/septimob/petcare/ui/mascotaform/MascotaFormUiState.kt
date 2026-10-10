
package com.am.septimob.petcare.ui.mascotaform

data class MascotaFormUiState(
    val id: Long? = null,
    val nombre: String = "",
    val especie: String = "",
    val raza: String = "",
    val fechaNacimiento: Long? = null,
    val sexo: String = "",
    val peso: String = "",
    val fotoUri: String? = null,
    val cargando: Boolean = false,
    val guardando: Boolean = false,
    val errorCarga: Boolean = false,
    val error: String? = null
) {
    val esEdicion: Boolean
        get() = id != null
}
