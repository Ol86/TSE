package com.plcoding.wearosstopwatch.presentation

import android.util.Log
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.ValueKey
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

class ECGTrackerListener(private val trackerType: HealthTrackerType) : HealthTracker.TrackerEventListener {


    var isDataCollecting = true
        set(value) {
            field = value
        }

    var trackerActive = true
        set(value) {
            field = value
        }

    override fun onDataReceived(list: List<DataPoint>) {
        Log.d("Button trackerActive ECG", trackerActive.toString())
        if (trackerActive) {
            Log.d("List", ":$list")
            Log.d("Button trackerActive ECG", "IN HEEEEEEEEEEEEEEEEEEEEEEEEEERE")
            for (dataPoint in list) {
                if (trackerActive) {
                    Log.d("Button trackerActive ECG", "DataPoint: $dataPoint")
                    Log.d("Button trackerActive ECG", "a: ${dataPoint.a}")
                    Log.d("Button trackerActive ECG", "b: ${dataPoint.b}")
                    Log.d("Button trackerActive ECG", "time: ${dataPoint.timestamp}")
                }

                val json = JSON()
                val allValues = ArrayList<String>()
                allValues.add(dataPoint.timestamp.toString())

                if (dataPoint.b.size == 6) {
                    //1st DataPoint
                    allValues.add(dataPoint.getValue(ValueKey.EcgSet.ECG).toString())
                    allValues.add(dataPoint.getValue(ValueKey.EcgSet.PPG_GREEN).toString())
                    allValues.add(dataPoint.getValue(ValueKey.EcgSet.LEAD_OFF).toString())
                    allValues.add(dataPoint.getValue(ValueKey.EcgSet.MAX_THRESHOLD).toString())
                    allValues.add(dataPoint.getValue(ValueKey.EcgSet.SEQUENCE).toString())
                    allValues.add(dataPoint.getValue(ValueKey.EcgSet.MIN_THRESHOLD).toString())
                }
                else if (dataPoint.b.size == 2) {
                    //6st DataPoint
                    allValues.add(dataPoint.getValue(ValueKey.EcgSet.ECG).toString())
                    allValues.add(dataPoint.getValue(ValueKey.EcgSet.PPG_GREEN).toString())
                }
                else {
                    allValues.add(dataPoint.getValue(ValueKey.EcgSet.ECG).toString())
                }

                json.dataToJSON("ecg", allValues)

                println("JSON Test")

                /*val file: File = File(this@ECGTrackerListener.filesDir, "trackerData.json")
                val fileWriter = FileWriter(file)
                val bufferedWriter = BufferedWriter(fileWriter)
                bufferedWriter.write(trackerData.toString())
                bufferedWriter.close()*/
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