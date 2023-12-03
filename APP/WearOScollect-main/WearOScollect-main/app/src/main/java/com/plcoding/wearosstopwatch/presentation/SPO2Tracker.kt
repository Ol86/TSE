package com.plcoding.wearosstopwatch.presentation

// PpgGreenTracker.kt
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.HealthTrackerException
import com.samsung.android.service.health.tracking.HealthTrackingService
import com.samsung.android.service.health.tracking.data.HealthTrackerType

class SPO2Tracker(
    private val healthTracking: HealthTrackingService,
    private val trackerListener: HealthTracker.TrackerEventListener
) {
    private var SPO2Tracker: HealthTracker? = null

    init {
        connectTracker()
    }

    private fun connectTracker() {
        try {
            val availableTrackers: List<HealthTrackerType> =
                healthTracking.trackingCapability.supportHealthTrackerTypes

            if (availableTrackers.contains(HealthTrackerType.SPO2)) {
                SPO2Tracker = healthTracking.getHealthTracker(HealthTrackerType.SPO2)
                SPO2Tracker?.setEventListener(trackerListener)
            }
        } catch (e: HealthTrackerException) {
            // Handle connection error
        }
    }

    fun disconnectTracker() {
        try {
            SPO2Tracker?.flush()
            SPO2Tracker?.unsetEventListener()
            SPO2Tracker = null
        } catch (e: HealthTrackerException) {
            // Handle disconnection error
        }
    }

    // Other methods specific to PpgGreenTracker if needed
}
