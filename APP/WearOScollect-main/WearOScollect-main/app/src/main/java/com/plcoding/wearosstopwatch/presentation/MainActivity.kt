package com.plcoding.wearosstopwatch.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.samsung.android.service.health.tracking.ConnectionListener
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.HealthTrackerException
import com.samsung.android.service.health.tracking.HealthTrackingService
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.Value
import com.samsung.android.service.health.tracking.data.ValueKey
import java.util.concurrent.TimeUnit


class MainActivity : ComponentActivity() {

    lateinit var healthTracking : HealthTrackingService

    private val connectionListener = object : ConnectionListener {
        override fun onConnectionSuccess() {
            println("wwwwwwwwwwwwwwwwwwwwwwwwwwwww")
            Log.d("HealthTracker", "Connection success")
            //val availableTrackers = healthTrackingService.trackingCapability.supportHealthTrackerTypes

            val availableTrackers: List<HealthTrackerType> =
                healthTracking.trackingCapability.supportHealthTrackerTypes
            Log.d("HealthTracker", "Available trackers: $availableTrackers")

            if (availableTrackers.contains(HealthTrackerType.PPG_GREEN)) {
                ppgGreenTracker = healthTracking.getHealthTracker(HealthTrackerType.PPG_GREEN)
                ppgGreenTracker?.setEventListener(trackerListener)
                println("DATAAAAAAAAAAAAAAAAAAAAAAA")
                //ppgGreenTracker?.flush()
                //ppgGreenTracker?.unsetEventListener()

            }

            /*if (availableTrackers.contains((HealthTrackerType.ECG))) {
                ECGRateTracker = healthTracking.getHealthTracker(HealthTrackerType.ECG)
                ECGRateTracker?.setEventListener(trackerListener)
                println("ECGGGGGGGG")
            }*/

            /*val delayMillis = 40000 // 40 seconds

            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                if (availableTrackers.contains((HealthTrackerType.BIA))) {
                    heartRateTracker = healthTracking.getHealthTracker(HealthTrackerType.BIA)
                    heartRateTracker?.setEventListener(trackerListener)
                    println("BIAAAAAAAAAAAAAA")
                }
            }, delayMillis.toLong())*/
        }

        /*private val healthTrackingService: HealthTrackingService by lazy {
            HealthTrackingService(connectionListener, this@com.plcoding.wearosstopwatch.presentation.MainActivity)
        }*/

        private var ppgGreenTracker: HealthTracker? = null
        private var ECGRateTracker: HealthTracker? = null

        override fun onConnectionEnded() {
            Log.d("HealthTracker", "Connection ended")
        }

        override fun onConnectionFailed(e: HealthTrackerException) {
            println("ffffffffffffffffffffff")
            Log.e("HealthTracker", "Connection failed: ${e.errorCode}")
            if (e.hasResolution()) {
                e.resolve(this@MainActivity)
            }
        }
    }

    private val trackerListener = object : HealthTracker.TrackerEventListener {
        override fun onDataReceived(list: List<DataPoint>) {
            Log.d("List", ":$list")
            for (dataPoint in list) {
                Log.d("HealthTracker", "DataPoint: $dataPoint")
                //Log.d("HealthTracker", "HeartRate: $dataPoint")
                Log.d("HealthTracker", "a: ${dataPoint.a}")
                Log.d("HealthTracker", "b: ${dataPoint.b}")
                Log.d("HealthTracker", "time: ${dataPoint.timestamp}")

                //in .b is all relevant data
                for (entry in dataPoint.b) {
                    val key = entry.key
                    val value = entry.value.value.toString()

                    // Now you have the actual value
                    //Todo: differentiate between key types
                    println("Key: $key")
                    println("Value: $value")
                }


                // Update UI with received data
                // For example, you can use a Compose state to update the UI
            }
        }
        override fun onError(error: HealthTracker.TrackerError) {
            Log.e("HealthTracker", "Tracker error: ${error.name}")
        }

        override fun onFlushCompleted() {
            Log.d("HealthTracker", "Flushing data completed")
        }
    }

    private val WORK_TAG = "NotificationWorker"
    private val requestedPermissions = arrayOf(
        Manifest.permission.BODY_SENSORS,
        Manifest.permission.FOREGROUND_SERVICE,
        Manifest.permission.ACTIVITY_RECOGNITION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.WAKE_LOCK,
        Manifest.permission.POST_NOTIFICATIONS
    )
    private val promptFrequency = 1L
    private val promptFrequencyTimeUnit = TimeUnit.MINUTES
    private val initialDelay = 1L
    private val periodicWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
        promptFrequency,
        promptFrequencyTimeUnit
    )
        .setInitialDelay(initialDelay, promptFrequencyTimeUnit)
        .addTag("notification")
        .build()

    /*private var _healthTrackingService: HealthTrackingService? = null
    private val healthTrackingService: HealthTrackingService
        get() = _healthTrackingService ?: HealthTrackingService(connectionListener,
            this@com.plcoding.wearosstopwatch.presentation.MainActivity).also { _healthTrackingService = it }*/

    //private lateinit var healthTrackingService: HealthTrackingService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissions(requestedPermissions, 0)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BODY_SENSORS), 0)
        }

        println("MEEEEHHHHHHHHHHHHHHHHH.$connectionListener")
        healthTracking = HealthTrackingService(connectionListener, this@MainActivity)
        healthTracking.connectService()

        setContent {
            val viewModel = viewModel<StopWatchViewModel>()
            val timerState by viewModel.timerState.collectAsStateWithLifecycle()
            val stopWatchText by viewModel.stopWatchText.collectAsStateWithLifecycle()
            StopWatch(
                state = timerState,
                time = stopWatchText,
                notifications = "0",
                onStart = {startRoutine(viewModel)},
                onReset = {resetRoutine(viewModel)},
                modifier = Modifier.fillMaxSize()
            )
        }
    }
    private fun startRoutine(viewModel: StopWatchViewModel) {
        viewModel.start()
        WorkManager.getInstance(this).cancelAllWork()
        WorkManager.getInstance(this).enqueue(periodicWorkRequest)
    }
    private fun resetRoutine(viewModel: StopWatchViewModel) {
        viewModel.resetTimer()
        WorkManager.getInstance(this).cancelAllWork()
    }
}


@Composable
private fun StopWatch(
    state: TimerState,
    time: String,
    notifications: String,
    onStart: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = notifications,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Text(
                text = " / 10",
                fontSize = 15.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Bottom)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = time,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Text(
                text = " / 1:30:00",
                fontSize = 15.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Bottom)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = onStart,
                enabled = state != TimerState.RUNNING,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (state == TimerState.RUNNING) {
                        MaterialTheme.colors.surface
                    } else {
                        MaterialTheme.colors.primary
                    },
                )
            ) {
                if (state != TimerState.RUNNING) {
                    Icon(
                        imageVector = if (state == TimerState.RUNNING) {
                            Icons.Default.Pause
                        } else {
                            Icons.Default.PlayArrow
                        },
                        contentDescription = null
                    )
                }else {
                    val clickCount = remember { mutableStateOf(0) }
                    if (clickCount.value <= 2){
                        Chip(
                            onClick = {clickCount.value++},
                            enabled = state != TimerState.RESET,
                            label = {
                                Text(
                                    text = "Studie beendet",
                                    maxLines = 1, overflow = TextOverflow.Ellipsis
                                )
                            },
                            colors = ChipDefaults.primaryChipColors(backgroundColor = Color(0x99FFFFFF))
                        )
                    }else{
                        Chip(
                            onClick =onReset,
                            enabled = state != TimerState.RESET,
                            label = {
                                Text(
                                    text = "Sicher?",
                                    maxLines = 1, overflow = TextOverflow.Ellipsis
                                )
                            }
                        )
                    }
                }
            }
        }

    }
}