package com.am.septimob.petcare.ui.cuidadoform

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.am.septimob.petcare.data.repository.CuidadoRepository
import com.am.septimob.petcare.data.repository.MascotaRepository
import com.am.septimob.petcare.domain.model.Cuidado
import com.am.septimob.petcare.ui.navigation.PetCareRoute
import com.am.septimob.petcare.ui.components.hoyEnDias
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CuidadoFormViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: CuidadoRepository,
    private val mascotaRepository: MascotaRepository
) : ViewModel() {

    private val ruta = savedStateHandle.toRoute<PetCareRoute.CuidadoForm>()
    private val mascotaId: Long = ruta.mascotaId

    // Lo que el usuario ya eligió sobrevive a la muerte del proceso (el sistema cierra la app en segundo plano)
    private val _uiState = MutableStateFlow(
        CuidadoFormUiState(
            tipoCuidadoId = savedStateHandle[CLAVE_TIPO],
            fecha = savedStateHandle[CLAVE_FECHA] ?: hoyEnDias(),
            notas = savedStateHandle[CLAVE_NOTAS] ?: ""
        )
    )
    val uiState: StateFlow<CuidadoFormUiState> = _uiState

    // Avisa cuando se guardó o eliminó un cuidado
    private val _listo = Channel<Unit>(Channel.BUFFERED)
    val listo = _listo.receiveAsFlow()

    private var cuidadoCargado: Cuidado? = null

    init {
        observarTipos()
        observarMascota()
        // Si la ruta trae cuidadoId, es edición y se cargan los datos
        ruta.cuidadoId?.let(::cargarCuidado)
    }

    private fun observarMascota() {
        viewModelScope.launch {
            try {
                mascotaRepository.obtenerMascotaPorId(mascotaId).collect { mascota ->
                    _uiState.update {
                        it.copy(
                            mascotaNombre = mascota?.nombre ?: "",
                            mascotaFotoUri = mascota?.fotoUri
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                // Si falla, el formulario sigue funcionando sin mostrar la mascota
            }
        }
    }

    private fun observarTipos() {
        viewModelScope.launch {
            try {
                repository.obtenerTiposCuidado().collect { tipos ->
                    _uiState.update { it.copy(tiposCuidado = tipos) }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(error = "No se pudieron cargar los tipos de cuidado")
                }
            }
        }
    }

    fun actualizarTipo(tipoCuidadoId: Long) {
        savedStateHandle[CLAVE_TIPO] = tipoCuidadoId
        _uiState.update { it.copy(tipoCuidadoId = tipoCuidadoId, error = null) }
    }

    fun actualizarFecha(fecha: Long?) {
        savedStateHandle[CLAVE_FECHA] = fecha
        _uiState.update { it.copy(fecha = fecha, error = null) }
    }

    fun actualizarNotas(notas: String) {
        savedStateHandle[CLAVE_NOTAS] = notas
        _uiState.update { it.copy(notas = notas) }
    }

    fun cargarCuidado(id: Long) {
        if (_uiState.value.id == id || _uiState.value.cargando) return

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null) }

            try {
                val cuidado = repository.obtenerCuidadoPorId(id)

                if (cuidado == null) {
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            errorCarga = true,
                            error = "No se encontró el cuidado"
                        )
                    }
                    return@launch
                }

                cuidadoCargado = cuidado
                _uiState.update {
                    it.copy(
                        id = cuidado.id,
                        // Si el usuario ya había cambiado algo antes de que se cerrara el proceso, se respeta
                        tipoCuidadoId = savedStateHandle[CLAVE_TIPO] ?: cuidado.tipoCuidadoId,
                        fecha = savedStateHandle[CLAVE_FECHA] ?: cuidado.fecha,
                        notas = savedStateHandle[CLAVE_NOTAS] ?: cuidado.notas ?: "",
                        cargando = false
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        cargando = false,
                        errorCarga = true,
                        error = "No se pudo cargar el cuidado"
                    )
                }
            }
        }
    }

    fun guardarCuidado() {
        val estado = _uiState.value
        if (estado.guardando || estado.cargando || estado.errorCarga) return

        val tipoId = estado.tipoCuidadoId
        val fecha = estado.fecha

        if (tipoId == null) {
            _uiState.update { it.copy(error = "Debes seleccionar un tipo de cuidado") }
            return
        }
        if (fecha == null) {
            _uiState.update { it.copy(error = "Debes seleccionar una fecha") }
            return
        }

        _uiState.update { it.copy(guardando = true, error = null) }

        viewModelScope.launch {
            try {
                val cuidado = Cuidado(
                    id = estado.id ?: 0L,
                    mascotaId = mascotaId,
                    tipoCuidadoId = tipoId,
                    fecha = fecha,
                    notas = estado.notas.trim().ifBlank { null }
                )

                if (estado.esEdicion) {
                    repository.actualizarCuidado(cuidado)
                } else {
                    repository.insertarCuidado(cuidado)
                }

                _uiState.update { it.copy(guardando = false) }
                _listo.send(Unit)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(guardando = false, error = "No se pudo guardar el cuidado")
                }
            }
        }
    }

    fun eliminarCuidado() {
        val cuidado = cuidadoCargado ?: return
        if (_uiState.value.guardando) return

        _uiState.update { it.copy(guardando = true, error = null) }

        viewModelScope.launch {
            try {
                repository.eliminarCuidado(cuidado)
                _uiState.update { it.copy(guardando = false) }
                _listo.send(Unit)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(guardando = false, error = "No se pudo eliminar el cuidado")
                }
            }
        }
    }
}

private const val CLAVE_TIPO = "cuidado_tipo"
private const val CLAVE_FECHA = "cuidado_fecha"
private const val CLAVE_NOTAS = "cuidado_notas"
