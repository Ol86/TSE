package com.plcoding.wearosstopwatch.presentation

import org.json.JSONObject
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

class JSON {
    private val storedData: JSONObject?

    fun dataToJSON(trackerType: String, values: List<String>) {
        storedData?.apply {
            if (trackerType == "heartrate") {
                put("time", values[0])
                put("status", values[1])
                put("heartRate", values[2])
                put("heartRateIBI", values[3])
            }
            else if (trackerType == "ecg") {
                put("time", values[0])

                when (values.size) {
                    7 -> {
                        put("ecg", values[1])
                        put("ppgGreen", values[2])
                        put("leadOff", values[3])
                        put("maxThreshold", values[4])
                        put("sequence", values[5])
                        put("minThreshold", values[6])
                    }
                    3 -> {
                        put("ecg", values[1])
                        put("ppgGreen", values[2])
                    }
                    else -> {
                        put("ecg", values[1])
                    }
                }
            }
            else if (trackerType == "accelerometer") {
                put("time", values[0])
                put("x", values[1])
                put("y", values[2])
                put("z", values[3])
            }
            else if (trackerType == "ppggreen") {
                put("time", values[0])
                put("ppgGreen", values[1])
            }
            else if (trackerType == "ppgir") {
                put("time", values [0])
                put("ppgir", values[1])
            }
            else if (trackerType == "ppgred") {
                put("time", values [0])
                put("ppgred", values[1])
            }
            else if (trackerType == "spo2") {
                put("time", values[0])
                put("spo2", values[1])
                put("heartRate", values[2])
                put("status", values[3])
            }
        }
    }

    /*
    fun writeDatainJSONFile(trackerData) {
        val file: File = File(this@MainActivity.filesDir, "trackerData.json")
        val fileWriter = FileWriter(file)
        val bufferedWriter = BufferedWriter(fileWriter)
        bufferedWriter.write(trackerData.toString())
        bufferedWriter.close()
    }
     */
    init {
        storedData = null
    }
}