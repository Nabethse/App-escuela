package com.myapplication.features.alumn.data.datasource.local.dao

import androidx.room.*
import com.myapplication.features.alumn.data.datasource.local.entity.AlumnEntity
import kotlinx.coroutines.flow.Flow
//implementacion de room
@Dao
interface AlumnDao {
    @Query("SELECT * FROM alumns")
    fun getAllAlumns(): Flow<List<AlumnEntity>>

    @Query("SELECT * FROM alumns WHERE id = :id")
    suspend fun getAlumnById(id: Int): AlumnEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlumns(alumns: List<AlumnEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlumn(alumn: AlumnEntity)

    @Update
    suspend fun updateAlumn(alumn: AlumnEntity)

    @Delete
    suspend fun deleteAlumn(alumn: AlumnEntity)

    @Query("DELETE FROM alumns")
    suspend fun deleteAllAlumns()
}
