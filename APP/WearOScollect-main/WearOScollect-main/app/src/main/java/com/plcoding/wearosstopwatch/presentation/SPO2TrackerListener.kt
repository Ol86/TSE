package com.plcoding.wearosstopwatch.presentation

import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import com.plcoding.wearosstopwatch.presentation.database.SensorDataDatabase
import com.plcoding.wearosstopwatch.presentation.database.entities.PpgGreenData
import com.plcoding.wearosstopwatch.presentation.database.entities.Spo2Data
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.ValueKey
import kotlinx.coroutines.launch

class SPO2TrackerListener(private val trackerType: HealthTrackerType, private val json: JSON, private val db: SensorDataDatabase, coroutineScope: LifecycleCoroutineScope) : HealthTracker.TrackerEventListener {
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

                    allValues.add(dataPoint.getValue(ValueKey.SpO2Set.SPO2).toString())
                    allValues.add(dataPoint.getValue(ValueKey.SpO2Set.HEART_RATE).toString())
                    allValues.add(dataPoint.getValue(ValueKey.SpO2Set.STATUS).toString())
                    val spo2Data= Spo2Data(dataPoint.timestamp.toString(), dataPoint.getValue(ValueKey.SpO2Set.SPO2).toString(), dataPoint.getValue(ValueKey.SpO2Set.HEART_RATE).toString(),
                        dataPoint.getValue(ValueKey.SpO2Set.STATUS).toString(), "0")
                    scope.launch {
                        db.spo2Dao.upsertSpo2Data(spo2Data)
                    }

                    json.dataToJSON("spo2", allValues)

                    //(println("json spo2")
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