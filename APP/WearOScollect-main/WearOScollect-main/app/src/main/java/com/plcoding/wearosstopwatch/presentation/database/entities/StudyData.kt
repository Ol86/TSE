package com.plcoding.wearosstopwatch.presentation.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class StudyData(

    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var start_time: String?,
    var end_time: String?
)
