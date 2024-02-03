package com.plcoding.wearosstopwatch.presentation

import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import com.plcoding.wearosstopwatch.presentation.database.SensorDataDatabase
import com.plcoding.wearosstopwatch.presentation.database.entities.EcgData
import com.plcoding.wearosstopwatch.presentation.database.entities.PpgGreenData
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.ValueKey
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

class ECGTrackerListener(private val trackerType: HealthTrackerType, private val json: JSON, private val db: SensorDataDatabase, coroutineScope: LifecycleCoroutineScope) : HealthTracker.TrackerEventListener {
    private val scope = coroutineScope


    var isDataCollecting = true
        set(value) {
            field = value
        }

    var trackerActive = true
        set(value) {
            field = value
        }

    override fun onDataReceived(list: List<DataPoint>) {
        if (trackerActive) {
            for (dataPoint in list) {
                if (trackerActive) {
                    val allValues = ArrayList<String>()
                    allValues.add(dataPoint.timestamp.toString())
                    val ecgData: EcgData
                    if (dataPoint.b.size == 6) {
                        //1st DataPoint
                        allValues.add(dataPoint.getValue(ValueKey.EcgSet.ECG).toString())
                        allValues.add(dataPoint.getValue(ValueKey.EcgSet.PPG_GREEN).toString())
                        allValues.add(dataPoint.getValue(ValueKey.EcgSet.LEAD_OFF).toString())
                        allValues.add(dataPoint.getValue(ValueKey.EcgSet.MAX_THRESHOLD).toString())
                        allValues.add(dataPoint.getValue(ValueKey.EcgSet.SEQUENCE).toString())
                        allValues.add(dataPoint.getValue(ValueKey.EcgSet.MIN_THRESHOLD).toString())
                        ecgData = EcgData(dataPoint.timestamp.toString(), dataPoint.getValue(ValueKey.EcgSet.ECG).toString(),
                            dataPoint.getValue(ValueKey.EcgSet.PPG_GREEN).toString(), dataPoint.getValue(ValueKey.EcgSet.LEAD_OFF).toString(),
                            dataPoint.getValue(ValueKey.EcgSet.MAX_THRESHOLD).toString(), dataPoint.getValue(ValueKey.EcgSet.MIN_THRESHOLD).toString(),
                            dataPoint.getValue(ValueKey.EcgSet.SEQUENCE).toString(), "0")
                    } else if (dataPoint.b.size == 2) {
                        //6st DataPoint
                        allValues.add(dataPoint.getValue(ValueKey.EcgSet.ECG).toString())
                        allValues.add(dataPoint.getValue(ValueKey.EcgSet.PPG_GREEN).toString())
                        ecgData = EcgData(dataPoint.timestamp.toString(), dataPoint.getValue(ValueKey.EcgSet.ECG).toString(),
                            dataPoint.getValue(ValueKey.EcgSet.PPG_GREEN).toString(), "",
                            "", "",
                            "", "0")
                    } else {
                        allValues.add(dataPoint.getValue(ValueKey.EcgSet.ECG).toString())
                        ecgData = EcgData(dataPoint.timestamp.toString(), dataPoint.getValue(ValueKey.EcgSet.ECG).toString(),
                            "", "",
                            "", "",
                            "", "0")
                    }

                    json.dataToJSON("ecg", allValues)


                    scope.launch {
                        db.ecgDao.upsertEcgData(ecgData)
                    }

                    //println("JSON Test")

                    /*val file: File = File(this@ECGTrackerListener.filesDir, "trackerData.json")
                val fileWriter = FileWriter(file)
                val bufferedWriter = BufferedWriter(fileWriter)
                bufferedWriter.write(trackerData.toString())
                bufferedWriter.close()*/
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