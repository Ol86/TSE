package com.plcoding.wearosstopwatch.presentation

import android.util.Log
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.ValueKey

class PpgGreenTrackerListener(private val trackerType: HealthTrackerType) : HealthTracker.TrackerEventListener {

    var isDataCollecting = true
        set(value) {
            field = value
        }

    var trackerActive = true
        set(value) {
            field = value
        }

    override fun onDataReceived(list: List<DataPoint>) {
//        Log.d("Button trackerActive PPGGreen", trackerActive.toString())
        if (trackerActive) {
            Log.d("List", ":$list")
            Log.d("Button trackerActive PPGGreen", "IN HEEEEEEEEEEEEEEEEEEEEEEEEEERE")
            for (dataPoint in list) {
                if (trackerActive) {
//                    Log.d("Button trackerActive PPGGreen", "DataPoint: $dataPoint")
//                    Log.d("Button trackerActive PPGGreen", "a: ${dataPoint.a}")
                    Log.d("Button trackerActive PPGGreen", "b: ${dataPoint.b}")
//                    Log.d("Button trackerActive PPGGreen", "time: ${dataPoint.timestamp}")

                    val json = JSON()
                    val allValues = ArrayList<String>()
                    allValues.add(dataPoint.timestamp.toString())

                    allValues.add(dataPoint.getValue(ValueKey.PpgGreenSet.PPG_GREEN).toString())

                    json.dataToJSON("ppggreen", allValues)
                    Log.d("Json Green", allValues.toString())
                    println("json pggGreen")
                }
            }
        }
    }

    override fun onError(error: HealthTracker.TrackerError) {
        Log.e("HealthTracker", "Tracker error: ${error.name}")
    }

    override fun onFlushCompleted() {
        Log.d("HealthTracker", "Flushing data completed")
    }
}