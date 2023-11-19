package com.plcoding.wearosstopwatch.presentation.database.daos

import androidx.room.*
import com.plcoding.wearosstopwatch.presentation.database.entities.ActiveMeasurement

@Dao
interface ActiveMeasurementDao {

    @Insert
    suspend fun insert(active : ActiveMeasurement): Long

    @Insert
    suspend fun insertAll(activeList: List<ActiveMeasurement>): List<Long>
}