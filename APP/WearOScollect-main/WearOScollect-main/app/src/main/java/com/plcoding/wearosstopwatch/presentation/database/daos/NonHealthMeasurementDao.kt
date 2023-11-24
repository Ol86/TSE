package com.plcoding.wearosstopwatch.presentation.database.daos

import androidx.room.Dao
import androidx.room.Insert
import com.plcoding.wearosstopwatch.presentation.database.entities.NonHealthMeasurement

@Dao
interface NonHealthMeasurementDao{

    @Insert
    suspend fun insert(vararg study: NonHealthMeasurement): Long
}