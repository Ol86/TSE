package com.plcoding.wearosstopwatch.presentation.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SessionData(

    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var startTimeMillis: Long,
    var endTimeMillis: Long
)
