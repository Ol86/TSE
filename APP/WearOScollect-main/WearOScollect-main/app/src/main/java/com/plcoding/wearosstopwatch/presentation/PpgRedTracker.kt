package com.plcoding.wearosstopwatch.presentation

// PpgGreenTracker.kt
import android.util.Log
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.HealthTrackerException
import com.samsung.android.service.health.tracking.HealthTrackingService
import com.samsung.android.service.health.tracking.data.HealthTrackerType

class PpgRedTracker(
    private val healthTracking: HealthTrackingService,
    private val trackerListener: HealthTracker.TrackerEventListener
) {
    private var ppgRedTracker: HealthTracker? = null
    var trackerActive = true
        set(value) {
            field = value
        }

    init {
        connectTracker()
    }

    private fun connectTracker() {
        try {
            val availableTrackers: List<HealthTrackerType> =
                healthTracking.trackingCapability.supportHealthTrackerTypes

            if (availableTrackers.contains(HealthTrackerType.PPG_RED)) {
                ppgRedTracker = healthTracking.getHealthTracker(HealthTrackerType.PPG_RED)
                ppgRedTracker?.setEventListener(trackerListener)
                Log.i("TRed", "Active")
            }
        } catch (e: HealthTrackerException) {
            // Handle connection error
        }
    }

    fun disconnectTracker() {
        try {
            ppgRedTracker?.flush()
            ppgRedTracker?.unsetEventListener()
            ppgRedTracker = null
        } catch (e: HealthTrackerException) {
            // Handle disconnection error
        }
    }

    // Other methods specific to PpgGreenTracker if needed
}
