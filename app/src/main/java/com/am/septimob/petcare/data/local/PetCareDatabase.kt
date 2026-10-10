
package com.am.septimob.petcare.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.am.septimob.petcare.data.local.dao.CuidadoDao
import com.am.septimob.petcare.data.local.dao.MascotaDao
import com.am.septimob.petcare.data.local.dao.TipoCuidadoDao
import com.am.septimob.petcare.data.local.entity.CuidadoEntity
import com.am.septimob.petcare.data.local.entity.MascotaEntity
import com.am.septimob.petcare.data.local.entity.TipoCuidadoEntity

@Database(
    entities = [MascotaEntity::class, TipoCuidadoEntity::class, CuidadoEntity::class],
    version = 2,
    exportSchema = true
)
abstract class PetCareDatabase : RoomDatabase() {

    abstract fun mascotaDao(): MascotaDao
    abstract fun tipoCuidadoDao(): TipoCuidadoDao
    abstract fun cuidadoDao(): CuidadoDao
}
