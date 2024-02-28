package com.plcoding.wearosstopwatch.presentation
// PpgGreenTracker.kt
import android.util.Log
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.HealthTrackerException
import com.samsung.android.service.health.tracking.HealthTrackingService
import com.samsung.android.service.health.tracking.data.HealthTrackerType

class HeartRateTracker(
    private val healthTracking: HealthTrackingService,
    private val trackerListener: HealthTracker.TrackerEventListener
) {
    private var heartRateTracker: HealthTracker? = null

    init {
        connectTracker()
    }

    private fun connectTracker() {
        try {
            val availableTrackers: List<HealthTrackerType> =
                healthTracking.trackingCapability.supportHealthTrackerTypes

            if (availableTrackers.contains(HealthTrackerType.HEART_RATE)) {
                heartRateTracker = healthTracking.getHealthTracker(HealthTrackerType.HEART_RATE)
                heartRateTracker?.setEventListener(trackerListener)
                Log.i("THeartRate", "Active")
            }
        } catch (e: HealthTrackerException) {
            // Handle connection error
        }
    }

    fun disconnectTracker() {
        try {
            heartRateTracker?.flush()
            heartRateTracker?.unsetEventListener()
            heartRateTracker = null
        } catch (e: HealthTrackerException) {
            // Handle disconnection error
        }
    }

    // Other methods specific to PpgGreenTracker if needed
}
