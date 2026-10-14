package com.am.septimob.petcare.di

import android.content.Context
import androidx.room.Room
import com.am.septimob.petcare.data.local.PetCareDatabase
import com.am.septimob.petcare.data.local.TiposCuidadoIniciales
import com.am.septimob.petcare.data.local.dao.CuidadoDao
import com.am.septimob.petcare.data.local.dao.MascotaDao
import com.am.septimob.petcare.data.local.dao.TipoCuidadoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providePetCareDatabase(@ApplicationContext context: Context): PetCareDatabase {
        return Room.databaseBuilder(context, PetCareDatabase::class.java, "petcare_database")
            // Siembra los tipos de cuidado la primera vez que se crea la base
            .addCallback(TiposCuidadoIniciales)
            // No hay migraciones todavía: si cambia la versión, se recrea la base
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    @Provides
    fun provideMascotaDao(database: PetCareDatabase): MascotaDao = database.mascotaDao()

    @Provides
    fun provideTipoCuidadoDao(database: PetCareDatabase): TipoCuidadoDao = database.tipoCuidadoDao()

    @Provides
    fun provideCuidadoDao(database: PetCareDatabase): CuidadoDao = database.cuidadoDao()
}
