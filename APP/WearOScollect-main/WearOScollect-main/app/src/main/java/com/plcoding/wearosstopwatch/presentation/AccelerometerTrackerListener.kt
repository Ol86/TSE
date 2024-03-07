package com.plcoding.wearosstopwatch.presentation

import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import com.plcoding.wearosstopwatch.presentation.database.UserDataStore
import com.plcoding.wearosstopwatch.presentation.database.entities.AccelerometerData
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.ValueKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class AccelerometerTrackerListener(private val trackerType: HealthTrackerType, private val json: JSON,
                                   coroutineScope: LifecycleCoroutineScope, private val context: Context) : HealthTracker.TrackerEventListener {
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

                val allValues = ArrayList<String>()
                allValues.add(dataPoint.timestamp.toString())

                allValues.add(
                    dataPoint.getValue(ValueKey.AccelerometerSet.ACCELEROMETER_X).toString()
                )
                allValues.add(
                    dataPoint.getValue(ValueKey.AccelerometerSet.ACCELEROMETER_Y).toString()
                )
                allValues.add(
                    dataPoint.getValue(ValueKey.AccelerometerSet.ACCELEROMETER_Z).toString()
                )

                json.dataToJSON("accelerometer", allValues)

                val accelerometerData = AccelerometerData(dataPoint.timestamp.toString(), dataPoint.getValue(ValueKey.AccelerometerSet.ACCELEROMETER_X).toString(),
                    dataPoint.getValue(ValueKey.AccelerometerSet.ACCELEROMETER_Y).toString(), dataPoint.getValue(ValueKey.AccelerometerSet.ACCELEROMETER_Z).toString(), "0")
                val job = scope.launch {
                    UserDataStore.getUserRepository(context).accelerometerDao.upsertAccelerometerData(accelerometerData)
                }
                runBlocking(Dispatchers.IO) {
                    job.join()
                }

                //Log.i("Accelerometer", dataPoint.getValue(ValueKey.AccelerometerSet.ACCELEROMETER_X).toString())
                //println("json accelerometer")
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