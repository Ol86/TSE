package com.plcoding.wearosstopwatch.presentation.database.daos

import androidx.room.Dao
import androidx.room.Insert
import com.plcoding.wearosstopwatch.presentation.database.entities.InteractionMeasurement

@Dao
interface InteractionMeasurementDao {

    @Insert
    suspend fun insert(vararg: InteractionMeasurement) : Long
}