package com.plcoding.wearosstopwatch.presentation.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class EcgData (
    val time: String,
    val ecg: String,
    val ppgGreen: String,
    val leadOff: String,
    val maxThreshold: String,
    val minThreshold: String,
    val sequence: String,
    val sync: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0

)