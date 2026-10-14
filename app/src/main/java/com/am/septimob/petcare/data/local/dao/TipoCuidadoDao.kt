package com.am.septimob.petcare.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.am.septimob.petcare.data.local.entity.TipoCuidadoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TipoCuidadoDao {
    @Query("SELECT * FROM tipos_cuidado ORDER BY id")
    fun observarTodos(): Flow<List<TipoCuidadoEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarTodos(tipos: List<TipoCuidadoEntity>)
}