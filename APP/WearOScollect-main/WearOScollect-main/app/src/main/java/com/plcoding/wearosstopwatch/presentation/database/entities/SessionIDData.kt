package com.plcoding.wearosstopwatch.presentation.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SessionIDData (
    val session: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)