package com.plcoding.wearosstopwatch.presentation.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Spo2Data (
    val time: String,
    val spo2: String,
    val heartRate: String,
    val status: String,
    val sync: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
){
    fun toJsonMap(): Map<String, String> {
        return mapOf(
            "time" to time,
            "spo2" to spo2,
            "heartRate" to heartRate,
            "status" to status
        )
    }
}