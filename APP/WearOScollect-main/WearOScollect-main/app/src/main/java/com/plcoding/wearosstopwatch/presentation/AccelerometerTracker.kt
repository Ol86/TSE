package com.plcoding.wearosstopwatch.presentation

// PpgGreenTracker.kt
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.HealthTrackerException
import com.samsung.android.service.health.tracking.HealthTrackingService
import com.samsung.android.service.health.tracking.data.HealthTrackerType

class AccelerometerTracker(
    private val healthTracking: HealthTrackingService,
    private val trackerListener: HealthTracker.TrackerEventListener
) {
    private var accelerometerTracker: HealthTracker? = null

    init {
        connectTracker()
    }

    private fun connectTracker() {
        try {
            val availableTrackers: List<HealthTrackerType> =
                healthTracking.trackingCapability.supportHealthTrackerTypes

            if (availableTrackers.contains(HealthTrackerType.ACCELEROMETER)) {
                accelerometerTracker = healthTracking.getHealthTracker(HealthTrackerType.ACCELEROMETER)
                accelerometerTracker?.setEventListener(trackerListener)
            }
        } catch (e: HealthTrackerException) {
            // Handle connection error
        }
    }

    fun disconnectTracker() {
        try {
            accelerometerTracker?.flush()
            accelerometerTracker?.unsetEventListener()
            accelerometerTracker = null
        } catch (e: HealthTrackerException) {
            // Handle disconnection error
        }
    }

    // Other methods specific to PpgGreenTracker if needed
}
