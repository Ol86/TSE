package com.plcoding.wearosstopwatch.presentation

import android.util.Log
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.ValueKey
import java.sql.DriverManager.println
import java.util.ArrayList

class HeartRateTrackerListener(private val trackerType: HealthTrackerType) : HealthTracker.TrackerEventListener {

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
                    Log.d("MainActivity DataCollection HR", "DataPoint: $dataPoint")
                    Log.d("MainActivity DataCollection HR", "a: ${dataPoint.a}")
                    Log.d("MainActivity DataCollection HR", "b: ${dataPoint.b}")
                    Log.d("MainActivity DataCollection HR", "time: ${dataPoint.timestamp}")
                }

                val json = JSON()
                val allValues = ArrayList<String>()
                allValues.add(dataPoint.timestamp.toString())

                allValues.add(dataPoint.getValue(ValueKey.HeartRateSet.HEART_RATE).toString())

                json.dataToJSON("heartrate", allValues)

                println("json heartRate")
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
