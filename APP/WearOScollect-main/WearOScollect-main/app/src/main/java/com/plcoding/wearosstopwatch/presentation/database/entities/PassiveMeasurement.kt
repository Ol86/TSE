package com.plcoding.wearosstopwatch.presentation.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PassiveMeasurement (

    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var timestamp: Long,
    var step_count: Int,
    var distance: Double,
    var calories_burned: Double,
    var heart_rate: Double
)
