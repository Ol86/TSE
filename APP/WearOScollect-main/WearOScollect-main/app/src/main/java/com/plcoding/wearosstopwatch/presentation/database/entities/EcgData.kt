package com.plcoding.wearosstopwatch.presentation.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class EcgData (
    val time: String,
    val ecg: String,
    val ppgGreen: String,
    val leadOff: String,
    val maxThreshold: String,
    val minThreshold: String,
    val sequence: String,
    var sync: String,
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0

) {
    fun toJsonMap(): Map<String, String> {
        return mapOf(
            "time" to time,
            "ecg" to ecg,
            "ppgGreen" to ppgGreen,
            "leadOff" to leadOff,
            "maxThreshold" to maxThreshold,
            "minThreshold" to minThreshold,
            "sequence" to sequence
        )
    }
}