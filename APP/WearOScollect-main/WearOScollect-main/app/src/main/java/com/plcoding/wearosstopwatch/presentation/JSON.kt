package com.plcoding.wearosstopwatch.presentation

import org.json.JSONObject

class JSON {
    private val storedData: JSONObject?

    fun dataToJSON(trackerType: String, values: List<String>) {
        //trackerTypes have the same names as after "HealthTrackerType." only in lowercase
        storedData?.apply {
            if (trackerType == "heart_rate") {
                put("time", values[0])
                put("status", values[1])
                put("heartRate", values[2])
                put("heartRateIBI", values[3])
            }
            else if (trackerType == "ecg") {
                put("time", values[0])

                if (values.size == 7) {
                    put("ecg", values[1])
                    put("ppgGreen", values[2])
                    put("leadOff", values[3])
                    put("maxThreshold", values[4])
                    put("sequence", values[5])
                    put("minThreshold", values[6])
                }
                else if (values.size == 3) {
                    put("ecg", values[1])
                    put("ppgGreen", values[2])
                }
                else {
                    put("ecg", values[1])
                }
            }
        }
    }
    init {
        storedData = null
    }
}