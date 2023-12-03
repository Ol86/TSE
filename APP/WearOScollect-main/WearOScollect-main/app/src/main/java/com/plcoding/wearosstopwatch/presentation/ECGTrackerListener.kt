package com.plcoding.wearosstopwatch.presentation

import android.util.Log
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType

class ECGTrackerListener(private val trackerType: HealthTrackerType) : HealthTracker.TrackerEventListener {


    var isDataCollecting = true
        set(value) {
            field = value
        }

    override fun onDataReceived(list: List<DataPoint>) {
        Log.d("isDataCollectionRunning", isDataCollecting.toString())
        if (isDataCollecting) {
            Log.d("List", ":$list")
            Log.d("isDataCollectionRunning", "IN HEEEEEEEEEEEEEEEEEEEEEEEEEERE")
            for (dataPoint in list) {
                if (isDataCollecting) {
                    Log.d("MainActivity DataCollection ECG", "DataPoint: $dataPoint")
                    Log.d("MainActivity DataCollection ECG", "a: ${dataPoint.a}")
                    Log.d("MainActivity DataCollection ECG", "b: ${dataPoint.b}")
                    Log.d("MainActivity DataCollection ECG", "time: ${dataPoint.timestamp}")
                }

                for (entry in dataPoint.b) {
                    val key = entry.key
                    val value = entry.value.value.toString()

                    // Now you have the actual value
                    // Todo: differentiate between key types
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