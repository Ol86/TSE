package com.plcoding.wearosstopwatch.presentation.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HeartrateData (
    val time: String,
    val status: String,
    val sync: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
){
    fun toJsonMap(): Map<String, String> {
        return mapOf(
            "time" to time,
            "status" to status
        )
    }
}