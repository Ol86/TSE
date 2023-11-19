package com.plcoding.wearosstopwatch.presentation.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NonHealthMeasurement(
    @PrimaryKey(autoGenerate = true)
    var id: Long
)