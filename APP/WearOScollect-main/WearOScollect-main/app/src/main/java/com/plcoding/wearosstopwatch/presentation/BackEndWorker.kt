import android.app.usage.ConfigurationStats
import android.content.Context
import android.util.Log
import androidx.work.Configuration
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.Constraints
import androidx.work.NetworkType
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.plcoding.wearosstopwatch.presentation.api.ApiService
import com.plcoding.wearosstopwatch.presentation.database.UserDataStore
import com.plcoding.wearosstopwatch.presentation.database.entities.QuestionData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Timer
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timerTask

class BackEndWorker (contextInput: Context, params: WorkerParameters) : Worker(contextInput, params){

    private lateinit var context: Context
    private val scope = CoroutineScope(Dispatchers.Main)
    private var timer: Timer? = null
    private val intervalMillis = 60000 // Intervall in Millisekunden (hier 10 Sekunden)
    private var dbData: String = ""
    val uploadDataConstraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
    init {
        context = contextInput
    }

    override fun doWork(): Result {
        return try {
            runBlocking(Dispatchers.IO) {
                Log.i("WorkerTest1", "In runBlocking")
                startSending()
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    fun startSending() {
        timer = Timer()
        timer?.scheduleAtFixedRate(timerTask {
            Log.i("WorkerTest2", "In startSending")
            //connectApi()
            //TODO
        }, 0, intervalMillis.toLong())
    }

    fun stopSending() {
        timer?.cancel()
    }

    private fun dbDataTOJSON(): JsonObject {
        try {
            this.addQuestionData()
            val job = scope.launch {
                dbData = UserDataStore.getUserRepository(context).getLatestDataAsJson()
            }
            runBlocking(Dispatchers.IO) {
                job.join()
            }
        } catch (e: Exception){

            println("Error in Converting to Json!")
        }
        Log.i("DebuggingA1", dbData)
        val jsonObject: JsonObject = JsonParser().parse(dbData)
            .getAsJsonObject()
        Log.i("DebuggingA1", jsonObject.toString())
        return jsonObject
    }

    private fun addQuestionData(){
        var questionData: QuestionData
        val affectDataList = UserDataStore.getUserRepository(context).affectDao.getAllAffectData()
        try {
            affectDataList.forEach { element ->
                if (element.transferred == false) {
                    Log.i("DebuggingA35.1", element.transferred.toString())
                    Log.i("DebuggingA35.2", element.id.toString())
                    val time: String
                    val affect: String
                    val questionid: String
                    affect =
                        UserDataStore.getUserRepository(context).affectDao.getAffectDataByID(element.id).affect
                    questionid =
                        UserDataStore.getUserRepository(context).affectDao.getAffectDataByID(element.id).question
                    time =
                        UserDataStore.getUserRepository(context).notificationDao.getNotificationDataByID(
                            UserDataStore.getUserRepository(context).affectDao.getAffectDataByID(
                                element.id
                            ).notification_id
                        ).time
                    questionData = QuestionData(time, affect, questionid, "0")
                    Log.i("DebuggingA35.3", affect + " " + questionid + " " + time)

                    scope.launch {

                        Log.i("DebuggingA35.4", questionData.questionid)
                        UserDataStore.getUserRepository(context).questionDao.upsertQuestionData(questionData)
                        UserDataStore.getUserRepository(context).affectDao.markAsTransferred(element.id)
                    }
                } else {
                    Log.i("DebuggingA35.5", element.id.toString())
                }
            }
            /*val questionDataList =
                UserDataStore.getUserRepository(context).questionDao.getAllQuestionData()
            questionDataList.forEach { element ->
                if (element.sync == "0") {
                    Log.i("DebuggingA36.1", element.id.toString())
                    Log.i("DebuggingA36.2", element.sync)
                    val q: QuestionData = element
                    scope.launch {
                        UserDataStore.getUserRepository(context).questionDao.markAsSynced(
                            "1",
                            element.id
                        )
                    }
                }
            }*/
        }catch (e: Exception){
            println("Error in Adding QuestionData!")
        }
    }

    private fun connectApi(){
        val timeoutSeconds = 30// Set your desired timeout value in seconds
        val client = OkHttpClient.Builder()
            .readTimeout(timeoutSeconds.toLong(), TimeUnit.SECONDS)
            .writeTimeout(timeoutSeconds.toLong(), TimeUnit.SECONDS)
            .build()
        Log.i("sendDataApi0001", "Testing Timeout fix")
        val thread = Thread {
            try {
                Log.i("APImessage", "Connect")
                val retrofit = Retrofit.Builder()
                    .baseUrl("http://193.196.36.62:9000/")
                    .client(client)
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
                    Log.i("sendDataApi0", "Test")

                    if (tokenResponse.isSuccessful) {
                        val token = "Token " + tokenResponse.body()?.getAsJsonPrimitive("token")?.asString
                        //Log.i("sendDataApi1", token)

                        val postResponse = apiService.testPost(
                            this.dbDataTOJSON(),
                            //json.getStoredDataAsJsonObject(),
                            /*JsonObject().apply {
                                addProperty("test1", "Hello World")
                            },*/
                            token
                        ).execute()

                        if (postResponse.isSuccessful) {
                            //println(postResponse.body())
                            //Log.i("StoredDataApi",json.getStoredDataAsJsonObject().toString())
                            Log.i("sendDataApi2", postResponse.toString())
                            Log.i("sendDataApi3", "${postResponse.code()}")
                            Log.i("sendDataApi3", postResponse.body().toString())

                        } else {
                            Log.i("sendDataApi4", postResponse.toString())


                            Log.i("sendDataApi5", "${postResponse.code()}")
                            //Log.i("StoredDataApi",json.getStoredDataAsJsonObject().toString())
                        }
                    } else {
                        Log.i("sendDataApi6", "${tokenResponse.code()}")
                        Log.i("sendDataApi6,5", tokenResponse.toString())
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.i("sendDataApi7", "${e.printStackTrace()}")
                }
            } catch (e: java.lang.Exception) {
                Log.i("sendDataApi8", "${e.printStackTrace()}")
            }
        }
        thread.start()
        thread.join()
    }
}