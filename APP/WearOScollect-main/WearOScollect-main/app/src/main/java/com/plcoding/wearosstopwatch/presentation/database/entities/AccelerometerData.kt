package com.plcoding.wearosstopwatch.presentation.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AccelerometerData (
    val time: String,
    val x: String,
    val y: String,
    val z: String,
    val sync: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)