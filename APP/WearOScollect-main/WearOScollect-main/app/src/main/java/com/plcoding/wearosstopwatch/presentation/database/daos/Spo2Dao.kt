package com.plcoding.wearosstopwatch.presentation.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.plcoding.wearosstopwatch.presentation.database.entities.Spo2Data
import kotlinx.coroutines.flow.Flow

@Dao
interface Spo2Dao {
    @Upsert
    suspend fun upsertSpo2Data(spo2Data: Spo2Data)

    @Delete
    suspend fun deleteSpo2Data(spo2Data: Spo2Data)

    @Query("SELECT * FROM spo2Data ORDER BY time ASC")
    fun getByTimeOrdered(): Flow<List<Spo2Data>>

    @Query("SELECT * FROM spo2Data ORDER BY sync ASC")
    fun getBySyncOrdered(): Flow<List<Spo2Data>>

    @Query("SELECT * FROM spo2Data ORDER BY time DESC LIMIT 1")
    fun getLatestSpo2Data(): List<Spo2Data>

    @Query("SELECT * FROM spo2Data WHERE sync = 0 ORDER BY id ASC")
    fun getAllLatestSpo2Data(): List<Spo2Data>

    @Query("UPDATE spo2data SET sync = :s WHERE id = :id")
    suspend fun markAsSynced(s: String, id: Long)
}