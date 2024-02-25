package com.plcoding.wearosstopwatch.presentation.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.plcoding.wearosstopwatch.presentation.database.entities.AccelerometerData
import kotlinx.coroutines.flow.Flow

@Dao
interface AccelerometerDao {
    @Upsert
    suspend fun upsertAccelerometerData(accelerometerData: AccelerometerData): Long

    @Delete
    suspend fun deleteData(accelerometerData: AccelerometerData)

    @Query("SELECT * FROM accelerometerData ORDER BY time ASC")
    fun getByTimeOrdered(): Flow<List<AccelerometerData>>

    @Query("SELECT * FROM accelerometerData ORDER BY sync ASC")
    fun getBySyncOrdered(): Flow<List<AccelerometerData>>

    @Query("SELECT * FROM accelerometerData ORDER BY time DESC LIMIT 1")
    fun getLatestAccelerometerData(): List<AccelerometerData>

    @Query("SELECT * FROM accelerometerData WHERE sync = 0 ORDER BY id ASC")
    fun getAllLatestAccelerometerData(): List<AccelerometerData>

    @Query("UPDATE accelerometerdata SET sync = :s WHERE id = :id")
    suspend fun markAsSynced(s: String, id: Long)
}