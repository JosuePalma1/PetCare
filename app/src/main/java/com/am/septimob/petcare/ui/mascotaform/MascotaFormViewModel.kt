
package com.am.septimob.petcare.ui.mascotaform

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.am.septimob.petcare.data.repository.MascotaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.viewModelScope
import com.am.septimob.petcare.domain.usecase.FormularioMascota
import com.am.septimob.petcare.domain.usecase.GuardarMascotaUseCase
import com.am.septimob.petcare.domain.usecase.ResultadoGuardado
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.first
import androidx.navigation.toRoute
import com.am.septimob.petcare.ui.navigation.PetCareRoute
import com.am.septimob.petcare.ui.components.hoyEnDias
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MascotaFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: MascotaRepository,
    private val guardarMascotaUseCase: GuardarMascotaUseCase
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


    fun guardarMascota() {
        val estado = _uiState.value
        if (estado.guardando || estado.cargando || estado.errorCarga) return

        _uiState.value = estado.copy(guardando = true, error = null)

        viewModelScope.launch {
            try {
                // La validación y el guardado viven en el caso de uso (capa de dominio)
                val resultado = guardarMascotaUseCase(
                    FormularioMascota(
                        id = estado.id,
                        nombre = estado.nombre,
                        especie = estado.especie,
                        raza = estado.raza,
                        fechaNacimiento = estado.fechaNacimiento,
                        sexo = estado.sexo,
                        peso = estado.peso,
                        fotoUri = estado.fotoUri
                    ),
                    hoyEnDias = hoyEnDias()
                )

                when (resultado) {
                    is ResultadoGuardado.Invalida -> _uiState.value = _uiState.value.copy(
                        guardando = false,
                        error = resultado.mensaje
                    )

                    ResultadoGuardado.Guardada -> {
                        _uiState.value = _uiState.value.copy(guardando = false)
                        _guardadoExitoso.send(Unit)
                    }
                }
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
