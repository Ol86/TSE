package com.plcoding.wearosstopwatch.presentation.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HeartrateData (
    val time: String,
    val interbeat: String,
    val heartrate: String,
    val status: String,
    val sync: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
){
    fun toJsonMap(): Map<String, String> {
        return mapOf(
            "time" to time,
            "interbeat" to interbeat,
            "heartrate" to heartrate,
            "status" to status
        )
    }
}