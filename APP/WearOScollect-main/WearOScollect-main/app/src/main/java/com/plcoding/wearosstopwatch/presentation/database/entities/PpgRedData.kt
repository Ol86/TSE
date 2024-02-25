package com.plcoding.wearosstopwatch.presentation.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PpgRedData (
    val time: String,
    val ppgred: String,
    var sync: String,
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
){
    fun toJsonMap(): Map<String, String> {
        return mapOf(
//            "id" to id.toString(),
            "time" to time,
            "ppgred" to ppgred
        )
    }
}