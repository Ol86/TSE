package com.plcoding.wearosstopwatch.presentation

import android.util.Log
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.ValueKey

class PpgIRTrackerListener(private val trackerType: HealthTrackerType) : HealthTracker.TrackerEventListener {

    var isDataCollecting = true
        set(value) {
            field = value
        }

    var trackerActive = true
        set(value) {
            field = value
        }

    override fun onDataReceived(list: List<DataPoint>) {
//        Log.d("Button trackerActive PIR", trackerActive.toString())
        if (trackerActive) {
            Log.d("List", ":$list")
            Log.d("isDataCollectionRunning", "IN HEEEEEEEEEEEEEEEEEEEEEEEEEERE")
            for (dataPoint in list) {
                if (trackerActive) {
//                    Log.d("Button trackerActive PIR", "DataPoint: $dataPoint")
//                    Log.d("Button trackerActive PIR", "a: ${dataPoint.a}")
                    Log.d("Button trackerActive PIR", "b: ${dataPoint.b}")
//                    Log.d("Button trackerActive PIR", "time: ${dataPoint.timestamp}")

                    val json = JSON()
                    val allValues = ArrayList<String>()
                    allValues.add(dataPoint.timestamp.toString())

                    allValues.add(dataPoint.getValue(ValueKey.PpgIrSet.PPG_IR).toString())

                    json.dataToJSON("ppgir", allValues)

                    println("json ppgIR")
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