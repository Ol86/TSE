package com.plcoding.wearosstopwatch.presentation.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AccelerometerData (
    val time: String,
    val x: String,
    val y: String,
    val z: String,
    val sync: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
){
    fun toJsonMap(): Map<String, String> {
        return mapOf(
            "time" to time,
            "x" to x,
            "y" to y,
            "z" to z
        )
    }
}