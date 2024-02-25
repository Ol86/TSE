package com.plcoding.wearosstopwatch.presentation.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.plcoding.wearosstopwatch.presentation.database.entities.EcgData
import kotlinx.coroutines.flow.Flow

@Dao
interface EcgDao {
    @Upsert
    suspend fun upsertEcgData(ecgData: EcgData)

    @Delete
    suspend fun deleteEcgData(ecgData: EcgData)

    @Query("SELECT * FROM ecgData ORDER BY time ASC")
    fun getByTimeOrdered(): Flow<List<EcgData>>

    @Query("SELECT * FROM ecgData ORDER BY sync ASC")
    fun getBySyncOrdered(): Flow<List<EcgData>>

    @Query("SELECT * FROM ecgData ORDER BY time DESC LIMIT 1")
    fun getLatestEcgData(): List<EcgData>

    @Query("SELECT * FROM ecgData WHERE sync = 0 ORDER BY id ASC")
    fun getAllLatestEcgData(): List<EcgData>

    @Query("UPDATE ecgdata SET sync = :s WHERE id = :id")
    suspend fun markAsSynced(s: String, id: Long)
}