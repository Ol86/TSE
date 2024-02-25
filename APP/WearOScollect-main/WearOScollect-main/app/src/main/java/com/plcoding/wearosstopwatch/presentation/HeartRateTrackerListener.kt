package com.plcoding.wearosstopwatch.presentation

import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import com.plcoding.wearosstopwatch.presentation.database.UserDataStore
import com.plcoding.wearosstopwatch.presentation.database.entities.HeartrateData
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.ValueKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.ArrayList

class HeartRateTrackerListener(private val trackerType: HealthTrackerType, private val json: JSON,
                               coroutineScope: LifecycleCoroutineScope, private val context: Context
) : HealthTracker.TrackerEventListener {
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

                    allValues.add(dataPoint.getValue(ValueKey.HeartRateSet.HEART_RATE).toString())
                    allValues.add(dataPoint.getValue(ValueKey.HeartRateSet.HEART_RATE_IBI).toString())
                    allValues.add(dataPoint.getValue(ValueKey.HeartRateSet.STATUS).toString())


                    val heartrateData = HeartrateData(dataPoint.timestamp.toString(),dataPoint.getValue(ValueKey.HeartRateSet.HEART_RATE_IBI).toString(),"0",
                        dataPoint.getValue(ValueKey.HeartRateSet.HEART_RATE).toString(),dataPoint.getValue(ValueKey.HeartRateSet.STATUS).toString(),"0")
                    val job = scope.launch {
                        //db.heartrateDao.upsertHeartrateData(heartrateData)
                        UserDataStore.getUserRepository(context).heartrateDao.upsertHeartrateData(heartrateData)
                    }
                    runBlocking(Dispatchers.IO) {
                        job.join()
                    }

                    Log.i("HRS", dataPoint.getValue(ValueKey.HeartRateSet.HEART_RATE).toString())
                    //println("json heartRate")
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
