package com.plcoding.wearosstopwatch.presentation

import com.google.gson.annotations.SerializedName

data class TemplateQuestion(
    val id: Int,
    val question: String,
    val button1: Boolean,
    val button1_text: String,
    val button2: Boolean,
    val button2_text: String,
    val button3: Boolean,
    val button3_text: String,
    val button4: Boolean,
    val button4_text: String,
    @SerializedName("created_at") val createdAt: String
)
