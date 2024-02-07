package com.plcoding.wearosstopwatch.presentation

import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.plcoding.wearosstopwatch.presentation.database.SensorDataDatabase
import com.plcoding.wearosstopwatch.presentation.database.entities.PpgGreenData
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.ValueKey
import kotlinx.coroutines.launch

class PpgGreenTrackerListener(private val trackerType: HealthTrackerType,private val json: JSON,private val db: SensorDataDatabase, coroutineScope: LifecycleCoroutineScope) : HealthTracker.TrackerEventListener {

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

                    allValues.add(dataPoint.getValue(ValueKey.PpgGreenSet.PPG_GREEN).toString())

                    json.dataToJSON("ppggreen", allValues)
                   // Log.d("Json Green", allValues.toString())
                    val ppgGreenData = PpgGreenData(dataPoint.timestamp.toString(), dataPoint.getValue(ValueKey.PpgGreenSet.PPG_GREEN).toString(), "0")
                    scope.launch {
                        db.ppgGreenDao.upsertPpgGreenData(ppgGreenData)
                    }

                   // println("json pggGreen")
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