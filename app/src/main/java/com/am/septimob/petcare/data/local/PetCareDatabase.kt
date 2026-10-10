
package com.am.septimob.petcare.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.am.septimob.petcare.data.local.dao.MascotaDao
import com.am.septimob.petcare.data.local.entity.MascotaEntity

@Database(
    entities = [MascotaEntity::class],
    version = 1,
    exportSchema = true
)
abstract class PetCareDatabase : RoomDatabase() {

    abstract fun mascotaDao(): MascotaDao
}
