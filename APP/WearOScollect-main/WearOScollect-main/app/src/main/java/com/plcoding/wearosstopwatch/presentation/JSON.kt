package com.plcoding.wearosstopwatch.presentation

import android.util.Log
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

class JSON {
    private val storedData: JSONObject = JSONObject()

    fun getStoredDataAsJsonObject(): JSONObject {
        return storedData
    }



    fun dataToJSON(trackerType: String, values: List<String>) {
        storedData.apply {
            when (trackerType) {
                "heartrate" -> {
                    put("heartrate-time", values[0])
                    put("heartrate-status", values[1])
                    put("heartrate-heartRate", values[2])
                    put("heartrate-heartRateIBI", values[3])
                }
                "ecg" -> {
                    put("ecg-time", values[0])

                    when (values.size) {
                        7 -> {
                            put("ecg-ecg", values[1])
                            put("ecg-ppgGreen", values[2])
                            put("ecg-leadOff", values[3])
                            put("ecg-maxThreshold", values[4])
                            put("ecg-sequence", values[5])
                            put("ecg-minThreshold", values[6])
                        }

                        3 -> {
                            put("ecg-ecg", values[1])
                            put("ecg-ppgGreen", values[2])
                        }

                        else -> {
                            put("ecg-ecg", values[1])
                        }
                    }
                }
                "accelerometer" -> {
                    put("accelerometer-time", values[0])
                    put("accelerometer-x", values[1])
                    put("accelerometer-y", values[2])
                    put("accelerometer-z", values[3])
                }
                "ppggreen" -> {
                    put("ppggreen-time", values[0])
                    put("ppggreen-ppgGreen", values[1])
                }
                "ppgir" -> {
                    put("ppgir-time", values [0])
                    put("ppgir-ppgir", values[1])
                }
                "ppgred" -> {
                    put("ppgred-time", values [0])
                    put("ppgred-ppgred", values[1])
                }
                "spo2" -> {
                    put("spo2-time", values[0])
                    put("spo2-spo2", values[1])
                    put("spo2-heartRate", values[2])
                    put("spo2-status", values[3])
                }
            }
        }
        //Log.d("StoredData", storedData.toString())
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
}