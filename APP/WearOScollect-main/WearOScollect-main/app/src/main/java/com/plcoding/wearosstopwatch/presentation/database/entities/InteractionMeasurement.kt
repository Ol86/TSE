package com.plcoding.wearosstopwatch.presentation.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class InteractionMeasurement(

    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var notification_id: Long,
    var time_of_engagement: String,
    var time_of_completion: String
    )