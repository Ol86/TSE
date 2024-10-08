package com.plcoding.wearosstopwatch.presentation

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TemplateInfos(
    val id: Int,
    val max_time: Int,
    val title: String,
    //val watches: List<Watch>,
    val acc: Boolean,
    val hr: Boolean,
    @SerializedName("ppg_g") val ppgG: Boolean,
    @SerializedName("ppg_i") val ppgI: Boolean,
    @SerializedName("ppg_r") val ppgR: Boolean,
    val bia: Boolean,
    val ecg: Boolean,
    val spo2: Boolean,
    val swl: Boolean,
    @SerializedName("created_at") val createdAt: String,
    val question_interval: Int,
    val questions: List<TemplateQuestion>
) {
    fun getTrackerBooleans(): ArrayList<Boolean> {
        return arrayListOf(acc, ecg, hr, ppgG, ppgI, ppgR, spo2)
    }
}
