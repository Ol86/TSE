package com.plcoding.wearosstopwatch.presentation

import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import com.plcoding.wearosstopwatch.presentation.database.SensorDataDatabase
import com.plcoding.wearosstopwatch.presentation.database.entities.PpgGreenData
import com.plcoding.wearosstopwatch.presentation.database.entities.PpgRedData
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.ValueKey
import kotlinx.coroutines.launch

class PpgRedTrackerListener(private val trackerType: HealthTrackerType, private val json: JSON, private val db: SensorDataDatabase, coroutineScope: LifecycleCoroutineScope) : HealthTracker.TrackerEventListener {
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

                    allValues.add(dataPoint.getValue(ValueKey.PpgRedSet.PPG_RED).toString())

                    json.dataToJSON("ppgred", allValues)
                    val ppgRedData = PpgRedData(dataPoint.timestamp.toString(), dataPoint.getValue(ValueKey.PpgRedSet.PPG_RED).toString(), "0")
                    scope.launch {
                        db.ppgRedDao.upsertPpgRedData(ppgRedData)
                    }

                    //println("json ppgRed")
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