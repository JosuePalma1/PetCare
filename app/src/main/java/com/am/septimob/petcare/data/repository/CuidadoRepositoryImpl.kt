package com.am.septimob.petcare.data.repository

import com.am.septimob.petcare.data.local.dao.CuidadoDao
import com.am.septimob.petcare.data.local.dao.TipoCuidadoDao
import com.am.septimob.petcare.data.local.entity.CuidadoEntity
import com.am.septimob.petcare.data.local.entity.TipoCuidadoEntity
import com.am.septimob.petcare.domain.model.Cuidado
import com.am.septimob.petcare.domain.model.TipoCuidado
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CuidadoRepositoryImpl(
    private val cuidadoDao: CuidadoDao,
    private val tipoCuidadoDao: TipoCuidadoDao
) : CuidadoRepository {

    override fun obtenerCuidadosDeMascota(mascotaId: Long): Flow<List<Cuidado>> {
        return cuidadoDao.observarPorMascota(mascotaId).map { entidades ->
            entidades.map { it.toModel() }
        }
    }

    override suspend fun obtenerCuidadoPorId(id: Long): Cuidado? {
        return cuidadoDao.obtenerPorId(id)?.toModel()
    }

    override suspend fun insertarCuidado(cuidado: Cuidado): Long {
        return cuidadoDao.insertar(cuidado.toEntity())
    }

    override suspend fun actualizarCuidado(cuidado: Cuidado) {
        cuidadoDao.actualizar(cuidado.toEntity())
    }

    override suspend fun eliminarCuidado(cuidado: Cuidado) {
        cuidadoDao.eliminar(cuidado.toEntity())
    }

    override fun obtenerTiposCuidado(): Flow<List<TipoCuidado>> {
        return tipoCuidadoDao.observarTodos().map { entidades ->
            entidades.map { it.toModel() }
        }
    }
}

private fun CuidadoEntity.toModel() = Cuidado(
    id = id,
    mascotaId = mascotaId,
    tipoCuidadoId = tipoCuidadoId,
    fecha = fecha,
    notas = notas
)

private fun Cuidado.toEntity() = CuidadoEntity(
    id = id,
    mascotaId = mascotaId,
    tipoCuidadoId = tipoCuidadoId,
    fecha = fecha,
    notas = notas
)

private fun TipoCuidadoEntity.toModel() = TipoCuidado(
    id = id,
    nombre = nombre
)