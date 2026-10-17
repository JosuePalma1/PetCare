package com.am.septimob.petcare.ui.detallemascota

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.am.septimob.petcare.data.repository.CuidadoRepository
import com.am.septimob.petcare.data.repository.MascotaRepository
import com.am.septimob.petcare.data.repository.PreferenciasRepository
import com.am.septimob.petcare.domain.model.Cuidado
import com.am.septimob.petcare.domain.model.Mascota
import com.am.septimob.petcare.domain.model.OrdenHistorial
import com.am.septimob.petcare.domain.model.TipoCuidado
import com.am.septimob.petcare.ui.navigation.PetCareRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DetalleMascotaEvento {
    data object MascotaEliminada : DetalleMascotaEvento
    data class Error(val mensaje: String) : DetalleMascotaEvento
}

@HiltViewModel
class DetalleMascotaViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val mascotaRepository: MascotaRepository,
    private val cuidadoRepository: CuidadoRepository,
    private val preferenciasRepository: PreferenciasRepository
) : ViewModel() {

    private val mascotaId: Long =
        savedStateHandle.toRoute<PetCareRoute.DetalleMascota>().mascotaId

    private val _uiState =
        MutableStateFlow<DetalleMascotaUiState>(DetalleMascotaUiState.Loading)

    val uiState: StateFlow<DetalleMascotaUiState> = _uiState

    private val _eventos = Channel<DetalleMascotaEvento>()
    val eventos: Flow<DetalleMascotaEvento> = _eventos.receiveAsFlow()

    init {
        observarDetalle()
    }

    private fun observarDetalle() {
        viewModelScope.launch {
            try {
                combine(
                    mascotaRepository.obtenerMascotaPorId(mascotaId),
                    cuidadoRepository.obtenerCuidadosDeMascota(mascotaId),
                    cuidadoRepository.obtenerTiposCuidado(),
                    preferenciasRepository.preferencias
                ) { mascota, cuidados, tipos, preferencias ->
                    armarEstado(mascota, cuidados, tipos, preferencias.ordenHistorial)
                }.collect { estado ->
                    _uiState.value = estado
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.value = DetalleMascotaUiState.Error(
                    "No se pudo cargar la mascota"
                )
            }
        }
    }

    private fun armarEstado(
        mascota: Mascota?,
        cuidados: List<Cuidado>,
        tipos: List<TipoCuidado>,
        orden: OrdenHistorial
    ): DetalleMascotaUiState {
        if (mascota == null) {
            return DetalleMascotaUiState.Error("Mascota no encontrada")
        }
        val nombresTipo = tipos.associate { it.id to it.nombre }
        return DetalleMascotaUiState.Success(
            mascota = mascota,
            cuidados = cuidados.ordenados(orden).map { cuidado ->
                CuidadoItem(
                    id = cuidado.id,
                    tipoNombre = nombresTipo[cuidado.tipoCuidadoId] ?: "Cuidado",
                    fecha = cuidado.fecha,
                    notas = cuidado.notas
                )
            }
        )
    }

    fun eliminarMascota() {
        val estado = _uiState.value
        if (estado !is DetalleMascotaUiState.Success) return

        viewModelScope.launch {
            try {
                mascotaRepository.eliminarMascota(estado.mascota)
                _eventos.send(DetalleMascotaEvento.MascotaEliminada)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _eventos.send(
                    DetalleMascotaEvento.Error("No se pudo eliminar la mascota")
                )
            }
        }
    }
}

// Misma fecha: desempata el id para que el orden sea estable
private fun List<Cuidado>.ordenados(orden: OrdenHistorial): List<Cuidado> {
    val masAntiguoPrimero = compareBy<Cuidado>({ it.fecha }, { it.id })
    return when (orden) {
        OrdenHistorial.RECIENTES_PRIMERO -> sortedWith(masAntiguoPrimero.reversed())
        OrdenHistorial.ANTIGUOS_PRIMERO -> sortedWith(masAntiguoPrimero)
    }
}
