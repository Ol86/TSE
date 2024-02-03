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
    suspend fun upsertAccelerometerData(accelerometerData: AccelerometerData)

    @Delete
    suspend fun deleteData(accelerometerData: AccelerometerData)

    @Query("SELECT * FROM accelerometerData ORDER BY time ASC")
    fun getByTimeOrdered(): Flow<List<AccelerometerData>>

    @Query("SELECT * FROM accelerometerData ORDER BY sync ASC")
    fun getBySyncOrdered(): Flow<List<AccelerometerData>>
}