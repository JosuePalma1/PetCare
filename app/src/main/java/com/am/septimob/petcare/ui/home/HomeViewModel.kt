package com.am.septimob.petcare.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.am.septimob.petcare.data.repository.CuidadoRepository
import com.am.septimob.petcare.data.repository.MascotaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    mascotaRepository: MascotaRepository,
    cuidadoRepository: CuidadoRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        mascotaRepository.obtenerMascotas(),
        cuidadoRepository.obtenerTiposCuidado()
    ) { mascotas, tipos -> mascotas to tipos }
        .flatMapLatest { (mascotas, tipos) ->
            if (mascotas.isEmpty()) {
                flowOf<HomeUiState>(
                    HomeUiState.Success(
                        totalMascotas = 0,
                        totalCuidados = 0,
                        cuidadosRecientes = emptyList()
                    )
                )
            } else {
                combine(mascotas.map { mascota ->
                    cuidadoRepository.obtenerCuidadosDeMascota(mascota.id)
                }) { cuidadosPorMascota ->
                    val mascotaPorId = mascotas.associateBy { it.id }
                    val tipoPorId = tipos.associateBy { it.id }
                    val cuidados = cuidadosPorMascota.flatMap { it }
                    val recientes = cuidados
                        .sortedByDescending { it.fecha }
                        .mapNotNull { cuidado ->
                            val mascota = mascotaPorId[cuidado.mascotaId]
                                ?: return@mapNotNull null
                            CuidadoReciente(
                                id = cuidado.id,
                                mascotaId = mascota.id,
                                nombreMascota = mascota.nombre,
                                nombreTipo = tipoPorId[cuidado.tipoCuidadoId]?.nombre
                                    ?: "Cuidado",
                                fecha = cuidado.fecha,
                                notas = cuidado.notas
                            )
                        }
                        .take(2)

                    HomeUiState.Success(
                        totalMascotas = mascotas.size,
                        totalCuidados = cuidados.size,
                        cuidadosRecientes = recientes
                    ) as HomeUiState
                }
            }
        }
        .catch { emit(HomeUiState.Error("No se pudo cargar el resumen")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState.Loading
        )
}
