package com.plcoding.wearosstopwatch.presentation.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class QuestionData (
    val time: String,
    val answer: String,
    val questionid: String,
    val sync: String,
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
){
    fun toJsonMap(): Map<String, String> {
        return mapOf(
            "time" to time,
            "answer" to answer,
            "questionid" to questionid
        )
    }
}