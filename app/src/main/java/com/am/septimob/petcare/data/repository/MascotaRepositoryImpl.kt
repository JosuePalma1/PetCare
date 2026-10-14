
package com.am.septimob.petcare.data.repository

import com.am.septimob.petcare.data.local.dao.MascotaDao
import com.am.septimob.petcare.data.local.entity.MascotaEntity
import com.am.septimob.petcare.domain.model.Mascota
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MascotaRepositoryImpl(
    private val mascotaDao: MascotaDao
) : MascotaRepository {

    override fun obtenerMascotas(): Flow<List<Mascota>> {
        return mascotaDao.obtenerMascotas().map { entidades ->
            entidades.map { it.toModel() }
        }
    }

    override fun obtenerMascotaPorId(id: Long): Flow<Mascota?> {
        return mascotaDao.obtenerMascotaPorId(id).map { entidad ->
            entidad?.toModel()
        }
    }

    override suspend fun insertarMascota(mascota: Mascota): Long {
        return mascotaDao.insertarMascota(mascota.toEntity())
    }

    override suspend fun actualizarMascota(mascota: Mascota) {
        mascotaDao.actualizarMascota(mascota.toEntity())
    }

    override suspend fun eliminarMascota(mascota: Mascota) {
        mascotaDao.eliminarMascota(mascota.toEntity())
    }
}

private fun MascotaEntity.toModel() = Mascota(
    id = id,
    nombre = nombre,
    especie = especie,
    raza = raza,
    fechaNacimiento = fechaNacimiento,
    sexo = sexo,
    pesoKg = pesoKg,
    fotoUri = fotoUri
)

private fun Mascota.toEntity() = MascotaEntity(
    id = id,
    nombre = nombre,
    especie = especie,
    raza = raza,
    fechaNacimiento = fechaNacimiento,
    sexo = sexo,
    pesoKg = pesoKg,
    fotoUri = fotoUri
)
