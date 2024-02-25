package com.plcoding.wearosstopwatch.presentation.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data  class PpgIRData (
    val time: String,
    val ppgir: String,
    var sync: String,
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
){
    fun toJsonMap(): Map<String, String> {
        return mapOf(
            "time" to time,
            "ppgir" to ppgir
        )
    }
}