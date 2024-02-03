package com.plcoding.wearosstopwatch.presentation.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PpgGreenData (
    val time: String,
    val ppgGreen: String,
    val sync: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)