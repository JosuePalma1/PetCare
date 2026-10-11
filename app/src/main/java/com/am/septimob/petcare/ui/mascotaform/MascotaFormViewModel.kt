
package com.am.septimob.petcare.ui.mascotaform

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.am.septimob.petcare.data.repository.MascotaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.viewModelScope
import com.am.septimob.petcare.domain.model.Mascota
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.first
import androidx.navigation.toRoute
import com.am.septimob.petcare.ui.navigation.PetCareRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MascotaFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: MascotaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MascotaFormUiState())

    val uiState: StateFlow<MascotaFormUiState> = _uiState

    // Evento que avisa cuando se guardó una mascota
    private val _guardadoExitoso = Channel<Unit>(Channel.BUFFERED)

    val guardadoExitoso = _guardadoExitoso.receiveAsFlow()

    init {
        // El id llega en la ruta: si viene, es edición y se cargan los datos
        savedStateHandle.toRoute<PetCareRoute.MascotaForm>().mascotaId?.let(::cargarMascota)
    }

    fun actualizarNombre(nombre: String) {
        _uiState.value = _uiState.value.copy(
            nombre = nombre,
            error = null
        )
    }

    fun actualizarEspecie(especie: String) {
        _uiState.value = _uiState.value.copy(
            especie = especie,
            error = null
        )
    }
    fun actualizarRaza(raza: String) {
        _uiState.value = _uiState.value.copy(raza = raza)
    }

    fun actualizarFechaNacimiento(fecha: Long?) {
        _uiState.value = _uiState.value.copy(fechaNacimiento = fecha)
    }

    fun actualizarSexo(sexo: String) {
        _uiState.value = _uiState.value.copy(sexo = sexo)
    }

    fun actualizarPeso(peso: String) {
        _uiState.value = _uiState.value.copy(
            peso = peso,
            error = null
        )
    }

    fun actualizarFotoUri(uri: String?) {
        _uiState.value = _uiState.value.copy(fotoUri = uri)
    }


    fun cargarMascota(id: Long) {
        if (_uiState.value.id == id || _uiState.value.cargando) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                cargando = true,
                error = null
            )

            try {
                val mascota = repository.obtenerMascotaPorId(id).first()

                if (mascota == null) {
                    _uiState.value = _uiState.value.copy(
                        cargando = false,
                        errorCarga = true,
                        error = "No se encontró la mascota"
                    )
                    return@launch
                }

                _uiState.value = MascotaFormUiState(
                    id = mascota.id,
                    nombre = mascota.nombre,
                    especie = mascota.especie,
                    raza = mascota.raza,
                    fechaNacimiento = mascota.fechaNacimiento,
                    sexo = mascota.sexo,
                    peso = mascota.pesoKg?.toString() ?: "",
                    fotoUri = mascota.fotoUri
                )

            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(
                    cargando = false,
                    errorCarga = true,
                    error = "No se pudo cargar la mascota"
                )
            }
        }
    }


    private fun validarFormulario(): Boolean {
        val estado = _uiState.value

        if (estado.nombre.isBlank()) {
            _uiState.value = estado.copy(
                error = "Debes ingresar el nombre de la mascota"
            )
            return false
        }

        if (estado.especie.isBlank()) {
            _uiState.value = estado.copy(
                error = "Debes seleccionar una especie"
            )
            return false
        }

        if (estado.peso.isNotBlank()) {
            val peso = estado.peso.toDoubleOrNull()

            if (peso == null || !peso.isFinite() || peso <= 0) {
                _uiState.value = estado.copy(
                    error = "Ingresa un peso válido mayor que cero"
                )
                return false
            }
        }

        return true
    }


    fun guardarMascota() {
        if (
            _uiState.value.guardando ||
            _uiState.value.cargando ||
            _uiState.value.errorCarga
        ) return

        if (!validarFormulario()) return

        val estado = _uiState.value

        _uiState.value = estado.copy(
            guardando = true,
            error = null
        )

        viewModelScope.launch {
            try {
                val mascota = Mascota(
                    id = estado.id ?: 0L,
                    nombre = estado.nombre.trim(),
                    especie = estado.especie.trim(),
                    raza = estado.raza.trim(),
                    fechaNacimiento = estado.fechaNacimiento,
                    sexo = estado.sexo,
                    pesoKg = estado.peso.toDoubleOrNull(),
                    fotoUri = estado.fotoUri
                )

                if (estado.esEdicion) {
                    repository.actualizarMascota(mascota)
                } else {
                    repository.insertarMascota(mascota)
                }

                _uiState.value = _uiState.value.copy(
                    guardando = false
                )
                _guardadoExitoso.send(Unit)

            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(
                    guardando = false,
                    error = "No se pudo guardar la mascota"
                )
            }
        }
    }


}
