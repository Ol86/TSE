package com.plcoding.wearosstopwatch.presentation.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AffectData(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var notification_id: Long,
    var affect: String,
    var transferred: Boolean = false
)
