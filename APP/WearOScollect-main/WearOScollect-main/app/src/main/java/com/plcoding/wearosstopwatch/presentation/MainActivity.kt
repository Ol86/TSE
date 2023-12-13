package com.plcoding.wearosstopwatch.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.samsung.android.service.health.tracking.HealthTrackerException
import com.samsung.android.service.health.tracking.HealthTrackingService
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import java.util.concurrent.TimeUnit
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {

    lateinit var healthTracking : HealthTrackingService
    private lateinit var heartRateTracker: HeartRateTracker
    private lateinit var ppgGreenTracker: PpgGreenTracker
    private lateinit var ecgTracker: ECGTracker
    private lateinit var accelerometerTracker: AccelerometerTracker
    private lateinit var sPO2Tracker: SPO2Tracker
    private lateinit var ppgIRTracker: PpgIRTracker
    private lateinit var ppgRedTracker: PpgRedTracker
    private val ppgPpgGreenTrackerListener = PpgGreenTrackerListener(HealthTrackerType.PPG_GREEN)
    private val heartRateTrackerListener = HeartRateTrackerListener(HealthTrackerType.HEART_RATE)
    private val ecgTrackerListener = ECGTrackerListener(HealthTrackerType.ECG)
    private val accelerometerTrackerListener = AccelerometerTrackerListener(HealthTrackerType.ACCELEROMETER)
    private val sPO2TrackerListener = SPO2TrackerListener(HealthTrackerType.SPO2)
    private val ppgIRTrackerListener = PpgIRTrackerListener(HealthTrackerType.PPG_IR)
    private val ppgRedTrackerListener = PpgRedTrackerListener(HealthTrackerType.PPG_RED)

    private val connectionListener = object : ConnectionListener {
        override fun onConnectionSuccess() {
            println("wwwwwwwwwwwwwwwwwwwwwwwwwwwww")
            Log.d("HealthTracker", "Connection success")
            //val availableTrackers = healthTrackingService.trackingCapability.supportHealthTrackerTypes

            val availableTrackers: List<HealthTrackerType> =
                healthTracking.trackingCapability.supportHealthTrackerTypes
            Log.d("HealthTrackerList1", "Available trackers: $availableTrackers")

            if (availableTrackers.contains(HealthTrackerType.PPG_GREEN)) {
                ppgPpgGreenTrackerListener.isDataCollecting = isDataCollectionRunning1
                ppgGreenTracker = PpgGreenTracker(healthTracking, ppgPpgGreenTrackerListener)
            }

            if (availableTrackers.contains(HealthTrackerType.ECG)) {
                accelerometerTrackerListener.isDataCollecting = isDataCollectionRunning1
                ecgTracker = ECGTracker(healthTracking, ecgTrackerListener)
            }

            if (availableTrackers.contains(HealthTrackerType.ACCELEROMETER)) {
                accelerometerTrackerListener.isDataCollecting = isDataCollectionRunning1
                accelerometerTracker = AccelerometerTracker(healthTracking, accelerometerTrackerListener)
            }

            if (availableTrackers.contains(HealthTrackerType.SPO2)) {
                sPO2TrackerListener.isDataCollecting = isDataCollectionRunning1
                sPO2Tracker = SPO2Tracker(healthTracking, sPO2TrackerListener)
            }

            if (availableTrackers.contains(HealthTrackerType.PPG_IR)) {
                ppgIRTrackerListener.isDataCollecting = isDataCollectionRunning1
                ppgIRTracker = PpgIRTracker(healthTracking, ppgIRTrackerListener)
            }

            if (availableTrackers.contains(HealthTrackerType.PPG_RED)) {
                ppgRedTrackerListener.isDataCollecting = isDataCollectionRunning1
                ppgRedTracker = PpgRedTracker(healthTracking, ppgRedTrackerListener)
            }

            if (availableTrackers.contains(HealthTrackerType.HEART_RATE)) {
                heartRateTrackerListener.isDataCollecting = isDataCollectionRunning1
                heartRateTracker = HeartRateTracker(healthTracking, heartRateTrackerListener)
            }

        }

        override fun onConnectionEnded() {
            Log.d("HealthTracker", "Connection ended")
            val availableTrackers: List<HealthTrackerType> =
                healthTracking.trackingCapability.supportHealthTrackerTypes
            Log.d("HealthTrackerList2", "Available trackers: $availableTrackers")
            /*
                        if (availableTrackers.contains(HealthTrackerType.PPG_GREEN)) {
                            ppgGreenTracker?.flush()
                            Log.d("TrackerSSS2", trackerListener.toString())
                            ppgGreenTracker?.unsetEventListener()
                            println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++Connection-GREEN++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
                        }

                        if (availableTrackers.contains((HealthTrackerType.PPG_RED))) {
                            ppgREDTracker?.flush()
                            Log.d("TrackerSSS2", trackerListener.toString())
                            ppgREDTracker?.unsetEventListener()
                            println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++Connection-RED++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
                        }

                        if (availableTrackers.contains((HealthTrackerType.ECG))) {
                            ECGRateTracker?.flush()
                            Log.d("TrackerSSS2", trackerListener.toString())
                            ECGRateTracker?.unsetEventListener()
                            println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++Connection-ECG++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
                        }

                        if (availableTrackers.contains((HealthTrackerType.ACCELEROMETER))) {
                            accelerometer?.flush()
                            Log.d("TrackerSSS2", trackerListener.toString())
                            accelerometer?.unsetEventListener()
                            println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++Connection-ACC++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
                        }

                        if (availableTrackers.contains((HealthTrackerType.PPG_IR))) {
                            ppgIR?.flush()
                            Log.d("TrackerSSS2", trackerListener.toString())
                            ppgIR?.unsetEventListener()
                            println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++Connection-IR++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
                        }

                        if (availableTrackers.contains((HealthTrackerType.SPO2))) {
                            sPO2?.flush()
                            Log.d("TrackerSSS2", trackerListener.toString())
                            sPO2?.unsetEventListener()
                            println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++Connection-SPO2++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
                        }*/
        }

        override fun onConnectionFailed(e: HealthTrackerException) {
            println("ffffffffffffffffffffff")
            Log.e("HealthTracker", "Connection failed: ${e.errorCode}")
            if (e.hasResolution()) {
                e.resolve(this@MainActivity)
            }
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

    private var isDataCollectionRunning1 = false
    private var enabled1 = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissions(requestedPermissions, 0)

        /*healthTracking = HealthTrackingService(connectionListener, this@MainActivity)*/

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BODY_SENSORS), 0)
        }



        setContent {
            val viewModel = viewModel<StopWatchViewModel>()
            val timerState by viewModel.timerState.collectAsStateWithLifecycle()
            val stopWatchText by viewModel.stopWatchText.collectAsStateWithLifecycle()
            var currentView by remember { mutableStateOf(ViewType.StopWatch) }
            when (currentView) {
                ViewType.StopWatch -> {
                    StopWatch(
                        state = timerState,
                        time = stopWatchText,
                        notifications = "0",
                        onStart = {startRoutine(viewModel)},
                        onReset = {resetRoutine(viewModel)},
                        isDataCollectionRunning = isDataCollectionRunning1,
                        onStartDataCollection = {startDataCollection()},
                        onStopDataCollection = {stopDataCollection()},
                        arrayListOf(true, false, true, true, true, false),
                        onNavigateToSecondActivity = {currentView = ViewType.SecondActivity},
                        modifier = Modifier.fillMaxSize()
                    )
                }
                ViewType.SecondActivity -> {
                    SecondActivity(
                        enabled = enabled1,
                        onBack = { currentView = ViewType.StopWatch },
                        onAccOff = {accelerometerOff()},
                        onAccOn = {accelerometerOn()},
                        onPPGGreenOff = {ppgGreenOff()},
                        onPPGGreenOn = {ppgGreenOn()},
                        onPPGRedOff = {ppgRedOff()},
                        onPPGRedOn = {ppgRedOn()},
                        onPPGIROff = {ppgIROff()},
                        onPPGIROn = {ppIROn()},
                        onHeartRateOff = {heartRateOff()},
                        onHeartRateOn = {heartRateOn()},
                        onSPO2Off = {sPO2Off()},
                        onSPO2On = {sPO2On()}
                    )
                }
            }
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

    private fun startDataCollection() {
        try {
            Log.i(TAG, "Starting data collection. $connectionListener")
            healthTracking = HealthTrackingService(connectionListener, this@MainActivity)
            healthTracking.connectService()
            ppgGreenTracker = PpgGreenTracker(healthTracking, ppgPpgGreenTrackerListener)
            isDataCollectionRunning1 = true
            ppgPpgGreenTrackerListener.isDataCollecting = isDataCollectionRunning1
            ecgTrackerListener.isDataCollecting = isDataCollectionRunning1
            accelerometerTrackerListener.isDataCollecting = isDataCollectionRunning1
            sPO2TrackerListener.isDataCollecting = isDataCollectionRunning1
            ppgIRTrackerListener.isDataCollecting = isDataCollectionRunning1
            ppgRedTrackerListener.isDataCollecting = isDataCollectionRunning1
            heartRateTrackerListener.isDataCollecting = isDataCollectionRunning1
            Log.i(TAG, isDataCollectionRunning1.toString())
        } catch (e: Exception) {
            Log.e(TAG, "Error starting data collection: ${e.message}")
            // Handle the error appropriately (e.g., show a message to the user)
        }
    }

    private fun stopDataCollection() {
        try {
            Log.i(TAG, "Stopping data collection. $connectionListener")
            healthTracking.disconnectService()
            ppgGreenTracker.disconnectTracker()
            isDataCollectionRunning1 = false
            ppgPpgGreenTrackerListener.isDataCollecting = isDataCollectionRunning1
            ecgTrackerListener.isDataCollecting = isDataCollectionRunning1
            accelerometerTrackerListener.isDataCollecting = isDataCollectionRunning1
            sPO2TrackerListener.isDataCollecting = isDataCollectionRunning1
            ppgIRTrackerListener.isDataCollecting = isDataCollectionRunning1
            ppgRedTrackerListener.isDataCollecting = isDataCollectionRunning1
            heartRateTrackerListener.isDataCollecting = isDataCollectionRunning1
            Log.i(TAG, isDataCollectionRunning1.toString())
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping data collection: ${e.message}")
            // Handle the error appropriately (e.g., show a message to the user)
        }
    }

    companion object {
        private const val TAG = "MainActivity DataCollection"
    }

    private fun accelerometerOff(){
        Log.i("Button", "AccOff " + enabled1.toString())
        enabled1 = false
    }

    private fun accelerometerOn(){
        Log.i("Button", "AccOn " +enabled1.toString())
        enabled1 = true
    }

    private fun ppgGreenOff(){
        Log.i("Button", "ppgGreenOff ")
    }

    private fun ppgGreenOn(){
        Log.i("Button", "ppgGreenOn ")
    }

    private fun ppgRedOff(){
        Log.i("Button", "ppgRedOff ")
    }

    private fun ppgRedOn(){
        Log.i("Button", "ppgRedOn ")
    }

    private fun ppgIROff(){
        Log.i("Button", "ppgIROff ")
    }

    private fun ppIROn(){
        Log.i("Button", "ppIROn ")
    }

    private fun heartRateOff(){
        Log.i("Button", "heartRateOff ")
    }

    private fun heartRateOn(){
        Log.i("Button", "heartRateOn ")
    }

    private fun sPO2Off(){
        Log.i("Button", "sPO2Off ")
    }

        private fun sPO2On(){
        Log.i("Button", "sPO2On ")
    }


}

@Composable
private fun StopWatch(
    state: TimerState,
    time: String,
    notifications: String,
    onStart: () -> Unit,
    onReset: () -> Unit,
    isDataCollectionRunning: Boolean,
    onStartDataCollection: () -> Unit,
    onStopDataCollection: () -> Unit,
    tracker: ArrayList<Boolean>,    //Accelerometer,ECG,ppgGreen,ppgIR,ppgRed,SPO2
    onNavigateToSecondActivity: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Experiment 1",
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = time,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Text(
                text = " / 1:30:00",
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Bottom)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = notifications,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Text(
                text = " / 10",
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Bottom)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
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
                                    text = "Studie beenden",
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
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            if (isDataCollectionRunning) {
                Button(
                    onClick = {
                        onStopDataCollection()
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Red
                    )
                    /*colors = ButtonDefaults.buttonColors(
                        MaterialTheme.colors.surface
                    )*/
                ) {
                    Text("STOP")
                }
            } else {
                Button(
                    onClick = {
                        onStartDataCollection()
                    },
                    colors = ButtonDefaults.buttonColors(
                        MaterialTheme.colors.surface
                    )
                ) {
                    Text("Datenaufnahme")
                }
            }
        }

        Spacer(modifier = Modifier.height(50.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            if (tracker[0]) {
                Button(
                    onClick = {
                        tracker[0] = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Green
                    )
                ) {
                    Text("Acm")
                }
            }
            else {
                Button(
                    onClick = {
                        tracker[0] = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Red
                    )
                ) {
                    Text("Acm")
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = onNavigateToSecondActivity,
                // ...
            ) {
                Text("Go to Second Activity")
            }
        }

    }
}

@Composable
private fun SecondActivity(
    enabled: Boolean,
    onPPGGreenOff: () -> Unit,
    onPPGGreenOn: () -> Unit,
    onPPGRedOn: () -> Unit,
    onPPGRedOff: () -> Unit,
    onPPGIROff: () -> Unit,
    onPPGIROn: () -> Unit,
    onHeartRateOff: () -> Unit,
    onHeartRateOn: () -> Unit,
    onSPO2On: () -> Unit,
    onSPO2Off: () -> Unit,
    onBack: () -> Unit,
    onAccOff: () -> Unit,
    onAccOn: () -> Unit,
    modifier: Modifier = Modifier
) {
    var localEnabled1 by remember { mutableStateOf(true) }
    var localEnabled2 by remember { mutableStateOf(true) }
    var localEnabled3 by remember { mutableStateOf(true) }
    var localEnabled4 by remember { mutableStateOf(true) }
    var localEnabled5 by remember { mutableStateOf(true) }
    var localEnabled6 by remember { mutableStateOf(true) }
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = onBack,
                // ...
            ) {
                Text("Back to StopWatch")
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    // Fügen Sie hier den Code ein, den Sie ausführen möchten, wenn der Button geklickt wird
                    localEnabled1 = !localEnabled1 // Umkehrung des Werts von enabled
                    if (localEnabled1) {
                        onAccOn()
                    } else {
                        onAccOff()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (localEnabled1) Color.Green else Color.Red
                )
            ) {
                Text("Accelerometer")
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    // Fügen Sie hier den Code ein, den Sie ausführen möchten, wenn der Button geklickt wird
                    localEnabled2 = !localEnabled2 // Umkehrung des Werts von enabled
                    if (localEnabled2) {
                        onPPGGreenOn()
                    } else {
                        onPPGGreenOff()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (localEnabled2) Color.Green else Color.Red
                )
            ) {
                Text("PPGGreen")
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    // Fügen Sie hier den Code ein, den Sie ausführen möchten, wenn der Button geklickt wird
                    localEnabled3 = !localEnabled3 // Umkehrung des Werts von enabled
                    if (localEnabled3) {
                        onPPGRedOn()
                    } else {
                        onPPGRedOff()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (localEnabled3) Color.Green else Color.Red
                )
            ) {
                Text("PPGRed")
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    // Fügen Sie hier den Code ein, den Sie ausführen möchten, wenn der Button geklickt wird
                    localEnabled4 = !localEnabled4 // Umkehrung des Werts von enabled
                    if (localEnabled4) {
                        onPPGIROn()
                    } else {
                        onPPGIROff()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (localEnabled4) Color.Green else Color.Red
                )
            ) {
                Text("PPGIR")
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    // Fügen Sie hier den Code ein, den Sie ausführen möchten, wenn der Button geklickt wird
                    localEnabled5 = !localEnabled5 // Umkehrung des Werts von enabled
                    if (localEnabled5) {
                        onHeartRateOn()
                    } else {
                        onHeartRateOff()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (localEnabled5) Color.Green else Color.Red
                )
            ) {
                Text("HeartRate")
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    // Fügen Sie hier den Code ein, den Sie ausführen möchten, wenn der Button geklickt wird
                    localEnabled6 = !localEnabled6 // Umkehrung des Werts von enabled
                    if (localEnabled6) {
                        onSPO2On()
                    } else {
                        onSPO2Off()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (localEnabled6) Color.Green else Color.Red
                )
            ) {
                Text("SPO2")
            }
        }
    }
    // ...
}