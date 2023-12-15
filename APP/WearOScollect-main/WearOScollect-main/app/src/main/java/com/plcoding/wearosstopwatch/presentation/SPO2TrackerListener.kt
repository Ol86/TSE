package com.plcoding.wearosstopwatch.presentation

import android.util.Log
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.ValueKey

class SPO2TrackerListener(private val trackerType: HealthTrackerType) : HealthTracker.TrackerEventListener {

    var isDataCollecting = true
        set(value) {
            field = value
        }

    var trackerActive = true
        set(value) {
            field = value
        }

    override fun onDataReceived(list: List<DataPoint>) {
//        Log.d("Button trackerActive SPO", trackerActive.toString())
        if (trackerActive) {
            Log.d("List", ":$list")
            Log.d("isDataCollectionRunning", "IN HEEEEEEEEEEEEEEEEEEEEEEEEEERE")
            for (dataPoint in list) {
                if (trackerActive) {
//                    Log.d("Button trackerActive SPO", "DataPoint: $dataPoint")
//                    Log.d("Button trackerActive SPO", "a: ${dataPoint.a}")
                    Log.d("Button trackerActive SPO", "b: ${dataPoint.b}")
//                    Log.d("Button trackerActive SPO", "time: ${dataPoint.timestamp}")

                    val json = JSON()
                    val allValues = ArrayList<String>()
                    allValues.add(dataPoint.timestamp.toString())

                    allValues.add(dataPoint.getValue(ValueKey.SpO2Set.SPO2).toString())
                    allValues.add(dataPoint.getValue(ValueKey.SpO2Set.HEART_RATE).toString())
                    allValues.add(dataPoint.getValue(ValueKey.SpO2Set.STATUS).toString())

                    json.dataToJSON("spo2", allValues)

                    println("json spo2")
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