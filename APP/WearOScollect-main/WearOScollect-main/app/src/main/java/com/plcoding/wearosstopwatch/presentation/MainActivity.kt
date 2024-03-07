package com.plcoding.wearosstopwatch.presentation

import BackEndWorker
import DataSender
import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color.parseColor
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
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
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.plcoding.wearosstopwatch.presentation.api.ApiService
import com.plcoding.wearosstopwatch.presentation.database.UserDataStore
import com.plcoding.wearosstopwatch.presentation.database.entities.SessionIDData
import com.samsung.android.service.health.tracking.ConnectionListener
import com.samsung.android.service.health.tracking.HealthTrackerException
import com.samsung.android.service.health.tracking.HealthTrackingService
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock


/*
IMPORTANT:
The value of username has to be changed per watch that is used in
the experiment.
The value has to be one of the usernames you gave the watches in the backend
when you created them.
This has to be changed in the methods:
    @MainActivity class:
        - getTemplate()
        - getSession()
        - sendQuit()
    @BackEndWorker class:
        - sendData()

Sorry for the inconvenience
 */
class MainActivity : ComponentActivity(), LifecycleOwner {

    private val json = JSON()
    private var notifyCounter = 0
    private val gson = Gson()
    private var isDataCollectionRunning1 = false
    private val lock = ReentrantLock()

    lateinit var healthTracking : HealthTrackingService

    private lateinit var accelerometerTracker: AccelerometerTracker
    private lateinit var ecgTracker: ECGTracker
    private lateinit var heartRateTracker: HeartRateTracker
    private lateinit var ppgGreenTracker: PpgGreenTracker
    private lateinit var ppgIRTracker: PpgIRTracker
    private lateinit var ppgRedTracker: PpgRedTracker
    private lateinit var sPO2Tracker: SPO2Tracker

    private lateinit var accelerometerTrackerListener: AccelerometerTrackerListener
    private lateinit var ecgTrackerListener: ECGTrackerListener
    private lateinit var heartRateTrackerListener: HeartRateTrackerListener
    private lateinit var ppgGreenTrackerListener: PpgGreenTrackerListener
    private lateinit var ppgIRTrackerListener: PpgIRTrackerListener
    private lateinit var ppgRedTrackerListener: PpgRedTrackerListener
    private lateinit var sPO2TrackerListener: SPO2TrackerListener

    private lateinit var dataSender: DataSender

    //Default template so variables are not null at beginning
    //Gets overwritten when new template is synced
    private val defaultTemplate: String = """
    {
        "id": 31415,
        "max_time":0,
        "title": "{Default}",
        "watches": [
            {
                "name": "Watch2",
                "watch": "RFAW31MPVBJ"
            }
        ],
        "acc": true,
        "hr": true,
        "ppg_g": true,
        "ppg_i": true,
        "ppg_r": true,
        "bia": true,
        "ecg": true,
        "spo2": true,
        "swl": true,
        "created_at": "2024-02-10T16:37:04.512963+01:00",
        "question_interval":10,
        "questions": [
            {
                "id": 1,
                "question": "aaaaaaaaaaaaaa",
                "button1": true,
                "button1_text": "11111",
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
                "question": "bbbbbbbb",
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
                "question": "cccccccccccccc",
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
    private var activeTrackers = templateData.getTrackerBooleans()

    val connectionListener = object : ConnectionListener {
        override fun onConnectionSuccess() {
            Log.d("HealthTracker", "Connection success")

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
            Log.e("HealthTracker", "Connection failed: ${e.errorCode}")
            if (e.hasResolution()) {
                e.resolve(this@MainActivity)
            }
        }
    }

    private val requestedPermissions = arrayOf(
        Manifest.permission.BODY_SENSORS,
        Manifest.permission.FOREGROUND_SERVICE,
        Manifest.permission.ACTIVITY_RECOGNITION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.WAKE_LOCK,
        Manifest.permission.POST_NOTIFICATIONS
    )

    private fun createNotificationWorker(initialDelay: Long, promptFrequency: Long,
                                         templateData: TemplateInfos) : PeriodicWorkRequest {

        //promptFrequency min are 15min per worker instance

        val listType: Type = object : TypeToken<List<TemplateQuestion>>() {}.type
        val templateDataJson = gson.toJson(templateData.questions, listType)
        val templateQuestions = Data.Builder()
            .putString("template_questions", templateDataJson)
            .build()

        val promptFrequencyTimeUnit = TimeUnit.MINUTES

        val periodicWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            promptFrequency,
            promptFrequencyTimeUnit
        )
            .setInitialDelay(initialDelay, promptFrequencyTimeUnit)
            .addTag("notification")
            .setInputData(templateQuestions)
            .build()

        return periodicWorkRequest
    }

    private fun createBackEndWorker(initialDelay: Long, promptFrequency: Long) : PeriodicWorkRequest{
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val promptFrequencyTimeUnit = TimeUnit.MINUTES
        val periodicWorkRequest = PeriodicWorkRequestBuilder<BackEndWorker>(
            promptFrequency,
            promptFrequencyTimeUnit
            //repeatInterval = 10, // Repeat every 10 secs
            //repeatIntervalTimeUnit = TimeUnit.SECONDS
        )
            .setConstraints(constraints)
            .setInitialDelay(initialDelay, promptFrequencyTimeUnit)
            .addTag("BackEndWorker")
            .build()

        Log.i("BackendWorker", "created")
        return periodicWorkRequest
    }

    private fun setTracker(activeTrackerList: List<Boolean>) {
        accelerometerTrackerListener.trackerActive = activeTrackerList[0]
        ecgTrackerListener.trackerActive = activeTrackerList[1]
        heartRateTrackerListener.trackerActive = activeTrackerList[2]
        ppgGreenTrackerListener.trackerActive = activeTrackerList[3]
        ppgIRTrackerListener.trackerActive = activeTrackerList[4]
        ppgRedTrackerListener.trackerActive = activeTrackerList[5]
        sPO2TrackerListener.trackerActive = activeTrackerList[6]
    }

    //Check if watch got internet
    //Used for the green/red dot in stopwatch screen
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        return if (connectivityManager != null) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            capabilities != null && (
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                    )
        } else {
            false
        }
    }

    @SuppressLint("MutableCollectionMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val lifecycleScope = lifecycleScope
        accelerometerTrackerListener = AccelerometerTrackerListener(HealthTrackerType.ACCELEROMETER, json,lifecycleScope, applicationContext)
        ecgTrackerListener = ECGTrackerListener(HealthTrackerType.ECG, json, lifecycleScope, applicationContext)
        heartRateTrackerListener = HeartRateTrackerListener(HealthTrackerType.HEART_RATE, json, lifecycleScope, applicationContext)
        ppgGreenTrackerListener = PpgGreenTrackerListener(HealthTrackerType.PPG_GREEN, json, lifecycleScope, applicationContext)
        ppgIRTrackerListener = PpgIRTrackerListener(HealthTrackerType.PPG_IR, json, lifecycleScope, applicationContext)
        ppgRedTrackerListener = PpgRedTrackerListener(HealthTrackerType.PPG_RED, json, lifecycleScope, applicationContext)
        sPO2TrackerListener = SPO2TrackerListener(HealthTrackerType.SPO2, json, lifecycleScope, applicationContext)
        dataSender = DataSender(lifecycleScope, applicationContext)
        requestPermissions(requestedPermissions, 0)

        accelerometerTrackerListener.trackerActive = activeTrackers[0]
        ecgTrackerListener.trackerActive = activeTrackers[1]
        heartRateTrackerListener.trackerActive = activeTrackers[2]
        ppgGreenTrackerListener.trackerActive = activeTrackers[3]
        ppgIRTrackerListener.trackerActive = activeTrackers[4]
        ppgRedTrackerListener.trackerActive = activeTrackers[5]
        sPO2TrackerListener.trackerActive = activeTrackers[6]

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
            val notificationCounterState = remember { mutableStateOf(notifyCounter) }

            setTracker(activeTrackersState.value)

            /*
            This is for the notification counter
            Receives signal from @NotificationWorker in line40 by Filter: "ACTION_WORK_COMPLETED"
             */
            LaunchedEffect(Unit) {
                val broadcastReceiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        if (intent?.action == "ACTION_WORK_COMPLETED") {
                            notificationCounterState.value += 1
                            Log.i("Counter", "Counter Worked")
                        }
                    }
                }
                val filter = IntentFilter("ACTION_WORK_COMPLETED")
                applicationContext.registerReceiver(broadcastReceiver, filter)
            }

            when (currentView) {
                ViewType.StopWatch -> {
                    StopWatch(
                        state = timerState,
                        time = stopWatchText,
                        maxTime = templateDataState.value.max_time,
                        notifications = notificationCounterState.value.toString(),
                        onStart = { startRoutine(viewModel, templateDataState.value) },
                        onEndStudy = {currentView = ViewType.ConfirmActionScreen},
                        onBackToSettings = {currentView = ViewType.FirstScreen},
                        dataUploaded = isNetworkAvailable(applicationContext),
                        modifier = Modifier.fillMaxSize()
                    )
                }
                ViewType.FirstScreen -> {
                    FirstScreen(
                        onAccept = { currentView = ViewType.SecondActivity },
                        onSyncTemplates = { templateDataState.value = getTemplate()
                            activeTrackersState.value = templateDataState.value.getTrackerBooleans() },
                        templateData = templateDataState
                    )
                }
                ViewType.SecondActivity -> {
                    SecondActivity(
                        trackers = activeTrackersState,
                        onBack = { currentView = ViewType.FirstScreen },
                        onNext = { currentView = ViewType.StopWatch}
                    )
                }
                ViewType.ConfirmActionScreen -> {
                    ConfirmActionScreen(
                        onCancel = { currentView = ViewType.StopWatch},
                        onConfirm = { currentView = ViewType.StopWatch
                            resetRoutine(viewModel)
                            notificationCounterState.value = 0}
                    )
                }
            }
        }
    }
    private fun startRoutine(viewModel: StopWatchViewModel, templateData: TemplateInfos) {
        viewModel.start()
        WorkManager.getInstance(this).cancelAllWork()
        Log.i("TemplateData", templateData.questions[0].question)

        //Starts Workers with specified time intervals
        if (templateData.question_interval < 15) {
            val periodicWorkRequest = createNotificationWorker(templateData.question_interval.toLong(),
                templateData.question_interval.toLong() * 2, templateData)
            WorkManager.getInstance(this).enqueue(periodicWorkRequest)

            val periodicWorkRequestTheSecond = createNotificationWorker(templateData.question_interval.toLong() * 2,
                templateData.question_interval.toLong() * 2, templateData)
            WorkManager.getInstance(this).enqueue(periodicWorkRequestTheSecond)
        }
        else {
            val periodicWorkRequest = createNotificationWorker(templateData.question_interval.toLong(),
                templateData.question_interval.toLong(), templateData)
            WorkManager.getInstance(this).enqueue(periodicWorkRequest)
        }

        val periodicWorkRequestTheThird = createBackEndWorker(0, 1)
        WorkManager.getInstance(this).enqueue(periodicWorkRequestTheThird)

        getSession()
        startDataCollection()
    }

    private fun resetRoutine(viewModel: StopWatchViewModel) {
        Log.i("ResetRoutine", "1")
        stopDataCollection()
        Log.i("ResetRoutine", "2")
        WorkManager.getInstance(this).cancelAllWork()
        Log.i("ResetRoutine", "3")
        viewModel.resetTimer()
        Log.i("ResetRoutine", "4")
        sendQuit()
        Log.i("ResetRoutine", "5")
    }

    //Starts all health-trackers
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

    //Turns all health-trackers off
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
        }
    }

    /*
    Gets Template from Backend
    Returns instance of @TemplateInfos
     */
    private fun getTemplate(): TemplateInfos{
        var templateAsJsonString: String? = null

        val thread = Thread {
            //Configures Retrofit
            try {
                Log.i("APImessage", "Connect")
                val retrofit = Retrofit.Builder()
                    .baseUrl("http://193.196.36.62:9000/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val apiService: ApiService = retrofit.create(ApiService::class.java)

                //Gets API token
                try {
                    val tokenResponse = apiService.getToken(

                        /*
                        IMPORTANT:
                        The value of username has to be changed per watch that is used in
                        the experiment.
                        The value has to be one of the usernames you gave the watches in the backend
                        when you created them.
                        This has to be changed in the methods:
                            @MainActivity class:
                                - getTemplate()
                                - getSession()
                                - sendQuit()
                            @BackEndWorker class:
                                - sendData()

                        Sorry for the inconvenience
                         */
                        JsonObject().apply {
                            addProperty("username", "Watch2")
                            addProperty("password", "tse-KIT-2023")
                        }
                    ).execute()

                    //Gets template from backend
                    if (tokenResponse.isSuccessful) {
                        val token = "Token " + tokenResponse.body()?.getAsJsonPrimitive("token")?.asString
                        println(token)

                        val template = apiService.getTemplate(token).execute()

                        //used to get value of "templateAsJsonString" outside from thread
                        lock.lock()
                        try {
                            templateAsJsonString = template.body().toString().trimIndent()
                        } finally {
                            lock.unlock()
                        }

                        // Parse the received JSON
                        val gson = Gson()
                        val templateDataInstance: TemplateInfos =
                            gson.fromJson(templateAsJsonString, TemplateInfos::class.java)

                        // Access the parsed data
                        Log.i("Title:", templateDataInstance.title)
                        Log.i("max_Time:", templateDataInstance.max_time.toString())
                        Log.i("questionInterval:", templateDataInstance.question_interval.toString())
                        Log.i("Questions:", templateDataInstance.questions.toString())
                        println("Tracker, ${templateDataInstance.acc}" +
                                ", ${templateDataInstance.ecg}, ${templateDataInstance.hr}" +
                                ", ${templateDataInstance.ppgG}, ${templateDataInstance.ppgI}" +
                                ", ${templateDataInstance.ppgR}, ${templateDataInstance.spo2}")

                        if (template.isSuccessful) {
                            Log.i("GetTemplate", template.body().toString().trimIndent())
                        } else {
                            Log.w("GetTemplate", "${template.code()}")
                        }
                    } else {
                        Log.w("GetTemplate", "${tokenResponse.code()}")
                    }

                } catch (e: Exception) {
                    Log.e("GetTemplate", "${e.printStackTrace()}")
                }
            } catch (e: java.lang.Exception) {
                Log.e("GetTemplate", "${e.printStackTrace()}")
            }
        }

        thread.start()
        thread.join()

        //Receive value of "templateAsJsonString" to use outside of thread
        lock.lock()
        try {
            val valueFromThread = templateAsJsonString
            println("Value from thread: $valueFromThread")
        } finally {
            lock.unlock()
        }

        return gson.fromJson(templateAsJsonString, TemplateInfos::class.java)
    }

    /*
    Gets session from backend
    similar procedure as in getTemplate
     */
    /*
    IMPORTANT:
    The value of username has to be changed per watch that is used in
    the experiment.
    The value has to be one of the usernames you gave the watches in the backend
    when you created them.
    This has to be changed in the methods:
        @MainActivity class:
            - getTemplate()
            - getSession()
            - sendQuit()
        @BackEndWorker class:
            - sendData()

    Sorry for the inconvenience
     */
    private fun getSession(): String? {
        var sessionString: String? = null

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
                            addProperty("username", "Watch2")
                            addProperty("password", "tse-KIT-2023")
                        }
                    ).execute()

                    if (tokenResponse.isSuccessful) {
                        val token =
                            "Token " + tokenResponse.body()?.getAsJsonPrimitive("token")?.asString
                        println(token)

                        val session = apiService.getSession(token).execute()

                        lock.lock()
                        try {
                            sessionString = session.body().toString().trimIndent()
                        } finally {
                            lock.unlock()
                        }

                        if (session.isSuccessful) {
                            Log.i("GetSession", session.body().toString().trimIndent())
                        } else {
                            Log.w("GetSession", "${session.code()}")
                        }
                    } else {
                        Log.w("GetSession", "${tokenResponse.code()}")
                    }

                } catch (e: Exception) {
                    Log.e("GetSession", "${e.printStackTrace()}")
                }
            } catch (e: java.lang.Exception) {
                Log.e("GetSession", "${e.printStackTrace()}")
            }
        }

        thread.start()
        thread.join()

        lock.lock()
        try {
            val valueFromThread = sessionString
            println("Value from thread: $valueFromThread")
        } finally {
            lock.unlock()
        }
        val scope = lifecycleScope
        val pattern = Regex("\\d+")
        val matchResult = sessionString?.let { pattern.find(it) }
        val sessionID = matchResult?.value
        val sessionIDData = sessionID?.let { SessionIDData(it) }
        scope.launch {
            if (sessionIDData != null) {
                UserDataStore.getUserRepository(applicationContext).sessionIDDao.upsertSessionIDData(sessionIDData)
            }
        }
        return sessionID
    }

    /*
    IMPORTANT:
    The value of username has to be changed per watch that is used in
    the experiment.
    The value has to be one of the usernames you gave the watches in the backend
    when you created them.
    This has to be changed in the methods:
        @MainActivity class:
            - getTemplate()
            - getSession()
            - sendQuit()
        @BackEndWorker class:
            - sendData()

    Sorry for the inconvenience
     */
    private fun sendQuit() {
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
                            addProperty("username", "Watch2")
                            addProperty("password", "tse-KIT-2023")
                        }
                    ).execute()

                    if (tokenResponse.isSuccessful) {
                        val token = "Token " + tokenResponse.body()?.getAsJsonPrimitive("token")?.asString
                        println(token)

                        val quitMessage = apiService.sendQuit(token).execute()
                        Log.i("SendQuit", quitMessage.body().toString().trimIndent())
                        Log.i("SendQuit", "Done")
                    } else {
                        Log.i("SendQuit", "Failed")
                    }

                } catch (e: Exception) {
                    Log.e("SendQuit", "${e.printStackTrace()}")
                }
            } catch (e: java.lang.Exception) {
                Log.e("SendQuit", "${e.printStackTrace()}")
            }
        }
        thread.start()
        thread.join()
    }
    companion object {
        private const val TAG = "MainActivity DataCollection"
    }
    private fun accelerometerOff(){
        activeTrackers[0] = false
        accelerometerTrackerListener.trackerActive = false
        Log.i("Button", "Acc is " + false.toString())
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
        notificationManager.promptNotification(123456789, null)
        Log.i("OneTimeNotification", "Has been initialized")
    }
}

@Composable
private fun StopWatch(
    state: TimerState,
    time: String,
    maxTime: Int,
    notifications: String,
    onStart: () -> Unit,
    onEndStudy: () -> Unit,
    onBackToSettings: () -> Unit,
    dataUploaded: Boolean,
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
            if (maxTime != 0) {
                Text(
                   text = " / ${convertMinutesToHHMMSS(maxTime)}",
                   fontSize = 16.sp,
                   fontWeight = FontWeight.Light,
                   textAlign = TextAlign.Center,
                   modifier = Modifier.align(Alignment.Bottom)
               )
            }
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
                text = " Notifications",
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
                    )
                }
            }
            Spacer(modifier = Modifier.width(25.dp))

            Box(
                modifier = Modifier.padding(top = 15.dp)
            ) {
                if (state != TimerState.RUNNING) {
                    Canvas(modifier = Modifier.size(22.dp)) {
                        drawCircle(color = Color.DarkGray)
                    }
                } else {
                    Canvas(modifier = Modifier.size(22.dp)) {
                        drawCircle(
                            color = if (dataUploaded) Color(0xFF32CD32) else Color(0xFFAC3123)
                        )
                    }
                }
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

/*
Converts time from minutes to HHMMSS format, used to display maxTime of session
 */
private fun convertMinutesToHHMMSS(minutes: Int): String {
    val hours = minutes / 60
    val remainingMinutes = minutes % 60
    val seconds = 0

    return String.format("%02d:%02d:%02d", hours, remainingMinutes, seconds)
}

@Composable
private fun FirstScreen(
    onAccept: () -> Unit,
    onSyncTemplates: () -> Unit,
    templateData: MutableState<TemplateInfos>
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (templateData.value.id == 31415 && templateData.value.title == "{Default}") {
                    "No Template synchronised"
                } else {
                    "Currently is \"${templateData.value.title}\" synchronised"
                },
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(25.dp))
        Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { onSyncTemplates() },
                modifier = Modifier
                        .weight(1.5f)
                        .padding(horizontal = 5.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(parseColor("#0FADF0"))
                )
            ) {
                Text("Sync new Template",
                    textAlign = TextAlign.Center)
            }

            Button(
                onClick = {
                    if (templateData.value.id != 31415 && templateData.value.title != "{Default}") {
                        onAccept()
                    }
                },
                modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 5.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (templateData.value.id != 31415
                        && templateData.value.title != "{Default}") Color(parseColor("#32CD32"))
                    else Color(parseColor("#AC3123"))
                )
            ) {
                Text("Accept",
                    textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
private fun SecondActivity(
    trackers: MutableState<ArrayList<Boolean>>,
    onNext: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = onNext,
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 46.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(parseColor("#32CD32"))
                )
            ) {
                Text("Accept")
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {},
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
                onClick = {},
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
                onClick = {},
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
                onClick = {},
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
                onClick = {},
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
                onClick = {},
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
                onClick = {},
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
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
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
                Text("Back")
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