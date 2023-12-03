package com.plcoding.wearosstopwatch.presentation

// PpgGreenTracker.kt
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.HealthTrackerException
import com.samsung.android.service.health.tracking.HealthTrackingService
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType

class PpgIRTracker(
    private val healthTracking: HealthTrackingService,
    private val trackerListener: HealthTracker.TrackerEventListener
) {
    private var ppgIRTracker: HealthTracker? = null

    init {
        connectTracker()
    }

    private fun connectTracker() {
        try {
            val availableTrackers: List<HealthTrackerType> =
                healthTracking.trackingCapability.supportHealthTrackerTypes

            if (availableTrackers.contains(HealthTrackerType.PPG_IR)) {
                ppgIRTracker = healthTracking.getHealthTracker(HealthTrackerType.PPG_IR)
                ppgIRTracker?.setEventListener(trackerListener)
            }
        } catch (e: HealthTrackerException) {
            // Handle connection error
        }
    }

    fun disconnectTracker() {
        try {
            ppgIRTracker?.flush()
            ppgIRTracker?.unsetEventListener()
            ppgIRTracker = null
        } catch (e: HealthTrackerException) {
            // Handle disconnection error
        }
    }

    // Other methods specific to PpgGreenTracker if needed
}
