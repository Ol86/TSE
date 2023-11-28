package com.plcoding.wearosstopwatch.presentation.database.daos

import androidx.room.Dao
import androidx.room.Insert
import com.plcoding.wearosstopwatch.presentation.database.entities.PassiveMeasurement

@Dao
interface PassiveMeasurementDao {

    @Insert
    suspend fun insert(passive: PassiveMeasurement) : Long

}