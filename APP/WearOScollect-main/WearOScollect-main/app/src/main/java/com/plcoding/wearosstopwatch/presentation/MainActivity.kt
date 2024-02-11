package com.plcoding.wearosstopwatch.presentation

import DataSender
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Color.parseColor
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.UploadFile
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
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.plcoding.wearosstopwatch.presentation.api.ApiService
import com.plcoding.wearosstopwatch.presentation.database.SensorDataDatabase
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.locks.ReentrantLock

class MainActivity : ComponentActivity(), LifecycleOwner {

    private val json = JSON()
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            SensorDataDatabase::class.java,
            "sensorData.db"
        ).build()
    }

    lateinit var healthTracking : HealthTrackingService
    private lateinit var heartRateTracker: HeartRateTracker
    private lateinit var ppgGreenTracker: PpgGreenTracker
    private lateinit var ecgTracker: ECGTracker
    private lateinit var accelerometerTracker: AccelerometerTracker
    private lateinit var sPO2Tracker: SPO2Tracker
    private lateinit var ppgIRTracker: PpgIRTracker
    private lateinit var ppgRedTracker: PpgRedTracker
    private lateinit var ppgGreenTrackerListener: PpgGreenTrackerListener
    private lateinit var heartRateTrackerListener: HeartRateTrackerListener
    private lateinit var ecgTrackerListener: ECGTrackerListener
    private lateinit var accelerometerTrackerListener: AccelerometerTrackerListener
    private lateinit var sPO2TrackerListener: SPO2TrackerListener
    private lateinit var ppgIRTrackerListener: PpgIRTrackerListener
    private lateinit var ppgRedTrackerListener: PpgRedTrackerListener
    private lateinit var dataSender: DataSender
//    private val ppgGreenTrackerListener = PpgGreenTrackerListener(HealthTrackerType.PPG_GREEN, json, db)
//    private val heartRateTrackerListener = HeartRateTrackerListener(HealthTrackerType.HEART_RATE, json, db)
//    private val ecgTrackerListener = ECGTrackerListener(HealthTrackerType.ECG, json, db)
//    private val accelerometerTrackerListener = AccelerometerTrackerListener(HealthTrackerType.ACCELEROMETER, json, db)
//    private val sPO2TrackerListener = SPO2TrackerListener(HealthTrackerType.SPO2, json, db)
//    private val ppgIRTrackerListener = PpgIRTrackerListener(HealthTrackerType.PPG_IR, json, db)
//    private val ppgRedTrackerListener = PpgRedTrackerListener(HealthTrackerType.PPG_RED, json, db)



    private val connectionListener = object : ConnectionListener {
        override fun onConnectionSuccess() {
            println("wwwwwwwwwwwwwwwwwwwwwwwwwwwww")
            Log.d("HealthTracker", "Connection success")
            //val availableTrackers = healthTrackingService.trackingCapability.supportHealthTrackerTypes

            val availableTrackers: List<HealthTrackerType> =
                healthTracking.trackingCapability.supportHealthTrackerTypes
            Log.d("HealthTrackerList1", "Available trackers: $availableTrackers")

            if (availableTrackers.contains(HealthTrackerType.ACCELEROMETER)) {
                accelerometerTrackerListener.isDataCollecting = isDataCollectionRunning1
                accelerometerTrackerListener.trackerActive = activeTrackers[0]
                if(activeTrackers[0]) {
                    accelerometerTracker = AccelerometerTracker(healthTracking, accelerometerTrackerListener)
                }
            }

            if (availableTrackers.contains(HealthTrackerType.ECG)) {
                ecgTrackerListener.isDataCollecting = isDataCollectionRunning1
                ecgTrackerListener.trackerActive = activeTrackers[1]
                if(activeTrackers[1]) {
                    ecgTracker = ECGTracker(healthTracking, ecgTrackerListener)
                }
            }

            if (availableTrackers.contains(HealthTrackerType.HEART_RATE)) {
                heartRateTrackerListener.isDataCollecting = isDataCollectionRunning1
                heartRateTrackerListener.trackerActive = activeTrackers[2]
                if(activeTrackers[2]) {
                    heartRateTracker = HeartRateTracker(healthTracking, heartRateTrackerListener)
                }
            }

            if (availableTrackers.contains(HealthTrackerType.PPG_GREEN)) {
                ppgGreenTrackerListener.isDataCollecting = isDataCollectionRunning1
                ppgGreenTrackerListener.trackerActive = activeTrackers[3]
                if(activeTrackers[3]) {
                    ppgGreenTracker = PpgGreenTracker(healthTracking, ppgGreenTrackerListener)
                }
            }

            if (availableTrackers.contains(HealthTrackerType.PPG_IR)) {
                ppgIRTrackerListener.isDataCollecting = isDataCollectionRunning1
                ppgIRTrackerListener.trackerActive = activeTrackers[4]
                if(activeTrackers[4]) {
                    ppgIRTracker = PpgIRTracker(healthTracking, ppgIRTrackerListener)
                }
            }

            if (availableTrackers.contains(HealthTrackerType.PPG_RED)) {
                ppgRedTrackerListener.isDataCollecting = isDataCollectionRunning1
                ppgRedTrackerListener.trackerActive = activeTrackers[5]
                if(activeTrackers[5]) {
                    ppgRedTracker = PpgRedTracker(healthTracking, ppgRedTrackerListener)
                }
            }

            if (availableTrackers.contains(HealthTrackerType.SPO2)) {
                sPO2TrackerListener.isDataCollecting = isDataCollectionRunning1
                sPO2TrackerListener.trackerActive = activeTrackers[6]
                if(activeTrackers[6]) {
                    sPO2Tracker = SPO2Tracker(healthTracking, sPO2TrackerListener)
                }
            }
        }

        override fun onConnectionEnded() {
            Log.d("HealthTracker", "Connection ended")
            val availableTrackers: List<HealthTrackerType> =
                healthTracking.trackingCapability.supportHealthTrackerTypes
            Log.d("HealthTrackerList2", "Available trackers: $availableTrackers")
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

    private val initialDelay2 = 2L
    private val periodicWorkRequest_Second_Test = PeriodicWorkRequestBuilder<NotificationWorker>(
        promptFrequency,
        promptFrequencyTimeUnit
    )
        .setInitialDelay(initialDelay2, promptFrequencyTimeUnit)
        .addTag("notification")
        .build()

    /*private var _healthTrackingService: HealthTrackingService? = null
    private val healthTrackingService: HealthTrackingService
        get() = _healthTrackingService ?: HealthTrackingService(connectionListener,
            this@com.plcoding.wearosstopwatch.presentation.MainActivity).also { _healthTrackingService = it }*/

    //private lateinit var healthTrackingService: HealthTrackingService

    private var isDataCollectionRunning1 = false

    val gson = Gson()
    private val lock = ReentrantLock()
    private val defaultTemplate: String = """
    {
        "id": 31415,
        "title": "{Default}",
        "watches": [
            {
                "name": "Watch1",
                "watch": "11:22:33:44:55:66"
            }
        ],
        "acc": true,
        "hr": false,
        "ppg_g": true,
        "ppg_i": true,
        "ppg_r": true,
        "bia": true,
        "ecg": true,
        "spo2": true,
        "swl": true,
        "created_at": "2024-02-10T16:37:04.512963+01:00",
        "questions": [
            {
                "id": 1,
                "question": "Wie sind deine aktuellen Emotionen?",
                "button1": true,
                "button1_text": "Positiv",
                "button2": true,
                "button2_text": "Negativ",
                "button3": true,
                "button3_text": "Neutral",
                "button4": false,
                "button4_text": "",
                "created_at": "2024-02-10T16:36:00.218111+01:00"
            },
            {
                "id": 2,
                "question": "Wie fühlen sie sich?",
                "button1": true,
                "button1_text": "Gut",
                "button2": true,
                "button2_text": "Schlecht",
                "button3": true,
                "button3_text": "Gestresst",
                "button4": true,
                "button4_text": "Entspannt",
                "created_at": "2024-02-10T16:36:20.549078+01:00"
            },
            {
                "id": 3,
                "question": "Sind sie verärgert?",
                "button1": true,
                "button1_text": "Ja",
                "button2": true,
                "button2_text": "Nein",
                "button3": false,
                "button3_text": "",
                "button4": false,
                "button4_text": "",
                "created_at": "2024-02-10T16:36:31.426244+01:00"
            }
        ]
    }
""".trimIndent()

    private var templateData: TemplateInfos = gson.fromJson(defaultTemplate, TemplateInfos::class.java)

    //Accelerometer,ECG,HeartRate,ppgGreen,ppgIR,ppgRed,SPO2
    //private var activeTrackers = arrayListOf(true, true, true, true, true, true, true)
    private var activeTrackers = templateData.getTrackerBooleans()



    @SuppressLint("MutableCollectionMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val lifecycleScope = lifecycleScope
        ppgGreenTrackerListener = PpgGreenTrackerListener(HealthTrackerType.PPG_GREEN, json, db, lifecycleScope)
        heartRateTrackerListener = HeartRateTrackerListener(HealthTrackerType.HEART_RATE, json, db, lifecycleScope)
        ecgTrackerListener = ECGTrackerListener(HealthTrackerType.ECG, json, db, lifecycleScope)
        accelerometerTrackerListener = AccelerometerTrackerListener(HealthTrackerType.ACCELEROMETER, json, db, lifecycleScope)
        sPO2TrackerListener = SPO2TrackerListener(HealthTrackerType.SPO2, json, db, lifecycleScope)
        ppgIRTrackerListener = PpgIRTrackerListener(HealthTrackerType.PPG_IR, json, db, lifecycleScope)
        ppgRedTrackerListener = PpgRedTrackerListener(HealthTrackerType.PPG_RED, json, db, lifecycleScope)
        dataSender = DataSender(db, lifecycleScope)
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
            val templateDataState = remember { mutableStateOf(templateData) }
            val activeTrackersState = remember { mutableStateOf(activeTrackers) }
            var currentView by remember { mutableStateOf(ViewType.FirstScreen) }
            when (currentView) {
                ViewType.StopWatch -> {
                    StopWatch(
                        state = timerState,
                        time = stopWatchText,
                        notifications = "0",
                        onStart = {startRoutine(viewModel)},
                        onReset = {resetRoutine(viewModel)},
                        onEndStudy = {currentView = ViewType.ConfirmActionScreen},
                        isDataCollectionRunning = isDataCollectionRunning1,
                        onStartDataCollection = {startDataCollection()},
                        onStopDataCollection = {stopDataCollection()},
                        onBackToSettings = {currentView = ViewType.FirstScreen},
                        onConnectApi = {connectApi()},
                        activeTrackersState.value,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                ViewType.SecondActivity -> {
                    SecondActivity(
                        trackers = activeTrackersState,
                        onBack = { currentView = ViewType.FirstScreen },
                        onAccOff = {accelerometerOff()},
                        onAccOn = {accelerometerOn()},
                        onEcgOff = {ecgOff()},
                        onEcgOn = {ecgOn()},
                        onHeartRateOff = {heartRateOff()},
                        onHeartRateOn = {heartRateOn()},
                        onPPGGreenOff = {ppgGreenOff()},
                        onPPGGreenOn = {ppgGreenOn()},
                        onPPGIROff = {ppgIROff()},
                        onPPGIROn = {ppIROn()},
                        onPPGRedOff = {ppgRedOff()},
                        onPPGRedOn = {ppgRedOn()},
                        onSPO2Off = {sPO2Off()},
                        onSPO2On = {sPO2On()}
                    )
                }
                ViewType.FirstScreen -> {
                    FirstScreen(
                        onNavigateToSecondActivity = { currentView = ViewType.SecondActivity },
                        onAccept = { currentView = ViewType.StopWatch },
                        onSyncTemplates = { templateDataState.value = getTemplate()
                            activeTrackersState.value = templateDataState.value.getTrackerBooleans() },
                        templateData = templateDataState
                    )
                }
                ViewType.ConfirmActionScreen -> {
                    ConfirmActionScreen(
                        onCancel = { currentView = ViewType.StopWatch},
                        onConfirm = { currentView = ViewType.StopWatch
                            resetRoutine(viewModel) }
                    )
                }
            }
        }
    }
    private fun startRoutine(viewModel: StopWatchViewModel) {
        viewModel.start()
        WorkManager.getInstance(this).cancelAllWork()
        WorkManager.getInstance(this).enqueue(periodicWorkRequest)
        WorkManager.getInstance(this).enqueue(periodicWorkRequest_Second_Test)
        startDataCollection()
        startDataSending()
    }
    private fun resetRoutine(viewModel: StopWatchViewModel) {
        stopDataCollection()
        WorkManager.getInstance(this).cancelAllWork()
        viewModel.resetTimer()
    }

    private fun startDataCollection() {
        try {
            Log.i(TAG, "Starting data collection. $connectionListener")
            healthTracking = HealthTrackingService(connectionListener, this@MainActivity)
            healthTracking.connectService()
            isDataCollectionRunning1 = true
            Log.i(TAG, isDataCollectionRunning1.toString())
        } catch (e: Exception) {
            Log.e(TAG, "Error starting data collection: ${e.message}")
            // Handle the error appropriately (e.g., show a message to the user)
        }
    }

    private fun stopDataCollection() {
        try {
            Log.i(TAG, "Stopping data collection. $connectionListener")
            accelerometerOff()
            accelerometerTracker.disconnectTracker()
            ecgOff()
            ecgTracker.disconnectTracker()
            heartRateOff()
            heartRateTracker.disconnectTracker()
            ppgGreenOff()
            ppgGreenTracker.disconnectTracker()
            ppgIROff()
            ppgIRTracker.disconnectTracker()
            ppgRedOff()
            ppgRedTracker.disconnectTracker()
            sPO2Off()
            sPO2Tracker.disconnectTracker()

            healthTracking.disconnectService()
            isDataCollectionRunning1 = false
            Log.i(TAG, isDataCollectionRunning1.toString())
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping data collection: ${e.message}")
            // Handle the error appropriately (e.g., show a message to the user)
        }
    }

    private fun startDataSending() {
        dataSender.startSending()
    }

    private fun connectApi(){
        val thread = Thread {
            try {
                Log.i("APImessage", "Connect")
                val retrofit = Retrofit.Builder()
                    .baseUrl("http://193.196.36.62:9000/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val apiService: ApiService = retrofit.create(ApiService::class.java)

                try {
                    val tokenResponse = apiService.getToken(

                        JsonObject().apply {
                            addProperty("username", "Watch1")
                            addProperty("password", "tse-KIT-2023")
                        }
                    ).execute()

                    val a = JSONObject(
                            """
    {"session":1,"data":{"ecg":[{"time":"1707495249035","ecg":"-513617","ppgGreen":"378765","leadOff":"5","maxThreshold":"2794390","minThreshold":"-2804168","sequence":"4"}],"heartrate":[],"accelerometer":[{"time":"1707491100355","x":"309","y":"-249","z":"4076"}],"spo2":[{"time":"1707495256099","spo2":"0","heartRate":"0","status":"0"}],"ppgir":[{"time":"1707495255273","ppgir":"1707495255273"}],"ppgred":[{"time":"1707495255412","ppgred":"9432968"}],"ppggreen":[{"time":"1707493797898","ppgGreen":"1942357"}]}}
    """
                            )
                    Log.i("DebuggingA2", a.toString())

                    if (tokenResponse.isSuccessful) {
                        val token = "Token " + tokenResponse.body()?.getAsJsonPrimitive("token")?.asString
                        Log.i("StoredDataApi1", token)

                        /*val postResponse = apiService.testPost(
                            a,
                            //json.getStoredDataAsJsonObject(),
                            /*JsonObject().apply {
                                addProperty("test1", "Hello World")
                            },*/
                            token
                        ).execute()

                        if (postResponse.isSuccessful) {
                            //println(postResponse.body())
                            //Log.i("StoredDataApi",json.getStoredDataAsJsonObject().toString())
                            Log.i("StoredDataApi2", postResponse.toString())
                            Log.i("StoredDataApi3", "${postResponse.code()}")
                        } else {
                            println("Error: ${postResponse.code()}")
                            Log.i("StoredDataApi4", postResponse.toString())
                            Log.i("StoredDataApi5", "${postResponse.code()}")
                        }*/
                    } else {
                        println("Error: ${tokenResponse.code()}")
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        thread.start()

    }
    private fun getTemplate(): TemplateInfos{
        var templateAsJsonString: String? = null

        val thread = Thread {
            try {
                Log.i("APImessage", "Connect")
                val retrofit = Retrofit.Builder()
                    .baseUrl("http://193.196.36.62:9000/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val apiService: ApiService = retrofit.create(ApiService::class.java)

                try {
                    val tokenResponse = apiService.getToken(

                        JsonObject().apply {
                            addProperty("username", "Watch1")
                            addProperty("password", "tse-KIT-2023")
                        }
                    ).execute()

                    if (tokenResponse.isSuccessful) {
                        val token = "Token " + tokenResponse.body()?.getAsJsonPrimitive("token")?.asString
                        println(token)

                        val template = apiService.getTemplate(token).execute()

                        lock.lock()
                        try {
                            templateAsJsonString = template.body().toString().trimIndent()
                        } finally {
                            lock.unlock()
                        }

                        // Parse the JSON
                        val gson = Gson()
                        val templateDataInstance: TemplateInfos = gson.fromJson(templateAsJsonString, TemplateInfos::class.java)

                        // Access the parsed data
                        println("Title: ${templateDataInstance.title}")
                        println("Questions: ${templateDataInstance.questions}")

                        if (template.isSuccessful) {
                            Log.i("GetTemplateApi1", template.body().toString().trimIndent())
                        } else {
                            Log.i("GetTemplateApi2", "${template.code()}")
                        }
                    } else {
                        Log.i("GetTemplateApi3", "${tokenResponse.code()}")
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        thread.start()
        thread.join()

        lock.lock()
        try {
            val valueFromThread = templateAsJsonString
            println("Value from thread: $valueFromThread")
        } finally {
            lock.unlock()
        }

        return gson.fromJson(templateAsJsonString, TemplateInfos::class.java)
    }
    companion object {
        private const val TAG = "MainActivity DataCollection"
    }

    private fun accelerometerOff(){
        activeTrackers[0] = false
        accelerometerTrackerListener.trackerActive = activeTrackers[0]
        Log.i("Button", "Acc is " + activeTrackers[0].toString())
    }

    private fun accelerometerOn(){
        activeTrackers[0] = true
        accelerometerTrackerListener.trackerActive = activeTrackers[0]
        Log.i("Button", "Acc is " + activeTrackers[0].toString())
    }

    private fun ecgOff() {
        activeTrackers[1] = false
        ecgTrackerListener.trackerActive = activeTrackers[1]
        Log.i("Button", "ECG is " + activeTrackers[1].toString())
    }

    private fun ecgOn() {
        activeTrackers[1] = true
        ecgTrackerListener.trackerActive = activeTrackers[1]
        Log.i("Button", "Ecg is " + activeTrackers[1].toString())
    }

    private fun heartRateOff() {
        activeTrackers[2] = false
        heartRateTrackerListener.trackerActive = activeTrackers[2]
        Log.i("Button", "HeartRate is " + activeTrackers[2].toString())
    }

    private fun heartRateOn() {
        activeTrackers[2] = true
        heartRateTrackerListener.trackerActive = activeTrackers[2]
        Log.i("Button", "HeartRate is " + activeTrackers[2].toString())
    }

    private fun ppgGreenOff(){
        activeTrackers[3] = false
        ppgGreenTrackerListener.trackerActive = activeTrackers[3]
        Log.i("Button", "ppgGreen is " + activeTrackers[3].toString())
    }

    private fun ppgGreenOn(){
        activeTrackers[3] = true
        ppgGreenTrackerListener.trackerActive = activeTrackers[3]
        Log.i("Button", "ppgGreen is " + activeTrackers[3].toString())
    }

    private fun ppgIROff(){
        activeTrackers[4] = false
        ppgIRTrackerListener.trackerActive = activeTrackers[4]
        Log.i("Button", "ppgIR is " + activeTrackers[4].toString())
    }

    private fun ppIROn(){
        activeTrackers[4] = true
        ppgIRTrackerListener.trackerActive = activeTrackers[4]
        Log.i("Button", "ppgIR is " + activeTrackers[4].toString())
    }

    private fun ppgRedOff(){
        activeTrackers[5] = false
        ppgRedTrackerListener.trackerActive = activeTrackers[5]
        Log.i("Button", "ppgRed is " + activeTrackers[5].toString())
    }

    private fun ppgRedOn(){
        activeTrackers[5] = true
        ppgRedTrackerListener.trackerActive = activeTrackers[5]
        Log.i("Button", "ppgRed is " + activeTrackers[5].toString())
    }

    private fun sPO2Off(){
        activeTrackers[6] = false
        sPO2TrackerListener.trackerActive = activeTrackers[6]
        Log.i("Button", "SPO2 is " + activeTrackers[6].toString())
    }

    private fun sPO2On(){
        activeTrackers[6] = true
        sPO2TrackerListener.trackerActive = activeTrackers[6]
        Log.i("Button", "SPO2 is " + activeTrackers[6].toString())
    }
    private fun oneTimeNotification() {
        val notificationManager = NotificationManager(applicationContext)
        //notificationManager.oneTimeNotification("Hello", "World")
        notificationManager.promptNotification(123456789)
        Log.i("Notify", "HELOOOOOOOOOOOOOOOO")
    }
}

@Composable
private fun StopWatch(
    state: TimerState,
    time: String,
    notifications: String,
    onStart: () -> Unit,
    onReset: () -> Unit,
    onEndStudy: () -> Unit,
    isDataCollectionRunning: Boolean,
    onStartDataCollection: () -> Unit,
    onStopDataCollection: () -> Unit,
    onBackToSettings: () -> Unit,
    onConnectApi: () -> Unit,
    trackers: ArrayList<Boolean>,    //Accelerometer,ECG,HeartRate,ppgGreen,ppgIR,ppgRed,SPO2
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = time,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Bottom)
            )
            Text(
                text = " / 1:30:00",
                fontSize = 16.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Bottom)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(35.dp))
            Text(
                text = notifications,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Bottom)
            )
            Text(
                text = " / 10",
                fontSize = 20.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Bottom)
            )
        }
        Spacer(modifier = Modifier.height(15.dp))
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
                } else {
                    Chip(
                        onClick = {onEndStudy()},
                        label = {
                            Text(text = "End Study", color = Color.White,
                                maxLines = 1, overflow = TextOverflow.Ellipsis)
                        },
                        colors = ChipDefaults.chipColors(
                            backgroundColor = Color.DarkGray
                        )
                        //colors = ChipDefaults.primaryChipColors(backgroundColor = Color(0x99FFFFFF))
                    )
                }
            }
            Spacer(modifier = Modifier.width(15.dp))
            Button(
                onClick = { onConnectApi() },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (state == TimerState.RUNNING) {
                        Color(parseColor("#ED9620"))
                    } else {
                        Color(parseColor("#f4c079"))
                    },
                )
                ) {
                Icon(
                    imageVector = Icons.Default.UploadFile,
                    contentDescription = "Send Data"
                )
            }
        }

        if (state != TimerState.RUNNING) {
            Spacer(modifier = Modifier.height(15.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        onBackToSettings()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 35.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.DarkGray
                    )
                ) {
                    Text("Back to Settings")
                }
            }
        }
    }
}

@Composable
private fun SecondActivity(
    trackers: MutableState<ArrayList<Boolean>>,
    onAccOff: () -> Unit,
    onAccOn: () -> Unit,
    onEcgOff: () -> Unit,
    onEcgOn: () -> Unit,
    onHeartRateOff: () -> Unit,
    onHeartRateOn: () -> Unit,
    onPPGGreenOff: () -> Unit,
    onPPGGreenOn: () -> Unit,
    onPPGIROff: () -> Unit,
    onPPGIROn: () -> Unit,
    onPPGRedOn: () -> Unit,
    onPPGRedOff: () -> Unit,
    onSPO2On: () -> Unit,
    onSPO2Off: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    /*var localEnabled0 by remember { mutableStateOf(trackers[0]) }
    var localEnabled1 by remember { mutableStateOf(trackers[1]) }
    var localEnabled2 by remember { mutableStateOf(trackers[2]) }
    var localEnabled3 by remember { mutableStateOf(trackers[3]) }
    var localEnabled4 by remember { mutableStateOf(trackers[4]) }
    var localEnabled5 by remember { mutableStateOf(trackers[5]) }
    var localEnabled6 by remember { mutableStateOf(trackers[6]) }*/

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 46.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.DarkGray
                )
            ) {
                Text("      Back      ")
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    if (trackers.value[0]) {
                        onAccOff()
                    }
                    else {
                        onAccOn()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 46.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (trackers.value[0]) Color(parseColor("#0FADF0"))
                    else Color(parseColor("#AC3123"))
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
                    if (trackers.value[1]) {
                        onEcgOff()
                    }
                    else {
                        onEcgOn()
                    }
                    /*localEnabled1 = !localEnabled1
                    if (localEnabled1) {
                        onEcgOn()
                    } else {
                        onEcgOff()
                    }*/
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 46.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (trackers.value[1]) Color(parseColor("#0FADF0"))
                        else Color(parseColor("#AC3123"))
                )
            ) {
                Text("ECG")
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    if (trackers.value[2]) {
                        onHeartRateOff()
                    }
                    else {
                        onHeartRateOn()
                    }
                    /*val currentTrackers = trackers().value.toMutableList()

                    currentTrackers[2] = !currentTrackers[2]
                    trackers().value = currentTrackers as ArrayList<Boolean>*/
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 46.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (trackers.value[2]) Color(parseColor("#0FADF0"))
                    else Color(parseColor("#AC3123"))
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
                    if (trackers.value[3]) {
                        onPPGGreenOff()
                    }
                    else {
                        onPPGGreenOn()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 46.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (trackers.value[3]) Color(parseColor("#0FADF0"))
                    else Color(parseColor("#AC3123"))
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
                    if (trackers.value[4]) {
                        onPPGIROff()
                    }
                    else {
                        onPPGIROn()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 46.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (trackers.value[4]) Color(parseColor("#0FADF0"))
                    else Color(parseColor("#AC3123"))
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
                    if (trackers.value[5]) {
                        onPPGRedOff()
                    }
                    else {
                        onPPGRedOn()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 46.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (trackers.value[5]) Color(parseColor("#0FADF0"))
                    else Color(parseColor("#AC3123"))
                )
            ) {
                Text("PPGRed")
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    if (trackers.value[6]) {
                        onSPO2Off()
                    }
                    else {
                        onSPO2On()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 46.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (trackers.value[6]) Color(parseColor("#0FADF0"))
                    else Color(parseColor("#AC3123"))
                )
            ) {
                Text("SPO2")
            }
        }
    }
}

@Composable
private fun FirstScreen(
    onNavigateToSecondActivity: () -> Unit,
    onAccept: () -> Unit,
    onSyncTemplates: () -> Unit,
    modifier: Modifier = Modifier,
    templateData: MutableState<TemplateInfos>
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = templateData.value.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 12.dp, 16.dp, 12.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { onSyncTemplates() },
            ) {
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = "Sync Template"
                )
            }

            Spacer(modifier = Modifier.width(15.dp))
            Button(
                onClick = { onNavigateToSecondActivity() },
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Tracker Settings"
                )
            }
        }

        Spacer(modifier = Modifier.height(5.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    if (templateData.value.id != 31415 && templateData.value.title != "{Default}") {
                        onAccept()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 50.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (templateData.value.id != 31415
                        && templateData.value.title != "{Default}") Color(parseColor("#32CD32"))
                    else Color(parseColor("#AC3123"))
                    //backgroundColor = Color(parseColor("#32CD32"))
                )
            ) {
                Text("Accept")
            }
        }
    }
}

@Composable
private fun ConfirmActionScreen(
    onCancel: () -> Unit,
    onConfirm: () -> Unit
){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Are You Sure?",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { onCancel() },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFFAC3123)
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 7.dp)
            ) {
                Text("Cancel")
            }

            Button(
                onClick = { onConfirm() },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF32CD32)
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 7.dp)
            ) {
                Text("Confirm")
            }
        }
    }
}