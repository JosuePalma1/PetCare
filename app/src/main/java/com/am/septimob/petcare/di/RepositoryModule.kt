package com.am.septimob.petcare.di

import com.am.septimob.petcare.data.local.dao.CuidadoDao
import com.am.septimob.petcare.data.local.dao.MascotaDao
import com.am.septimob.petcare.data.local.dao.TipoCuidadoDao
import com.am.septimob.petcare.data.repository.CuidadoRepository
import com.am.septimob.petcare.data.repository.CuidadoRepositoryImpl
import com.am.septimob.petcare.data.repository.MascotaRepository
import com.am.septimob.petcare.data.repository.MascotaRepositoryImpl
import com.am.septimob.petcare.data.repository.PreferenciasRepository
import com.am.septimob.petcare.data.repository.PreferenciasRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideMascotaRepository(mascotaDao: MascotaDao): MascotaRepository =
        MascotaRepositoryImpl(mascotaDao)

    @Provides
    @Singleton
    fun provideCuidadoRepository(
        cuidadoDao: CuidadoDao,
        tipoCuidadoDao: TipoCuidadoDao
    ): CuidadoRepository = CuidadoRepositoryImpl(cuidadoDao, tipoCuidadoDao)

    @Provides
    @Singleton
    fun providePreferenciasRepository(impl: PreferenciasRepositoryImpl): PreferenciasRepository = impl
}
