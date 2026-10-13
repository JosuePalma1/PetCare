package com.am.septimob.petcare.domain.usecase

import com.am.septimob.petcare.data.repository.MascotaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** Regla de negocio del inicio: el resumen muestra cuántas mascotas existen. */
class ObtenerCantidadMascotasUseCase @Inject constructor(
    private val mascotaRepository: MascotaRepository
) {
    operator fun invoke(): Flow<Int> = mascotaRepository
        .obtenerMascotas()
        .map { mascotas -> mascotas.size }
}
