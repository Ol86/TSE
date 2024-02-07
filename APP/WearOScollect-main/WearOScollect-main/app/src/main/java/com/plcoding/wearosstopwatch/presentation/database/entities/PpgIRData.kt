package com.plcoding.wearosstopwatch.presentation.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data  class PpgIRData (
    val time: String,
    val ppgir: String,
    val sync: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)