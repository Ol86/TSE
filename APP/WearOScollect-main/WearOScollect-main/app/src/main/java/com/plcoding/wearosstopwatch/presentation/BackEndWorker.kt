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
                Log.i("BackendWorker", "In runBlocking")
                startSending()
            }
            Result.success()
        } catch (e: Exception) {
            Log.e("BackendWorker", "${e.printStackTrace()}")
            Result.failure()
        }
    }

    fun startSending() {
        timer = Timer()
        timer?.scheduleAtFixedRate(timerTask {
            Log.i("BackendWorker", "In startSending")
            connectApi()
        }, 0, intervalMillis.toLong())
    }

    fun stopSending() {
        timer?.cancel()
    }

    fun dbDataTOJSON(): JsonObject {
        try {
            this.addQuestionData()
            val job = scope.launch {
                dbData = UserDataStore.getUserRepository(context).getLatestDataAsJson()
            }
            runBlocking(Dispatchers.IO) {
                job.join()
            }
        } catch (e: Exception){
            Log.e("DbDataToJSON", "${e.printStackTrace()}")
        }
        Log.i("DbDataToJSON", "DB: $dbData")
        val jsonObject: JsonObject = JsonParser().parse(dbData)
            .getAsJsonObject()
        Log.i("DbDataToJSON", "JSON: $jsonObject")
        return jsonObject
    }

    private fun addQuestionData(){
        var questionData: QuestionData
        val affectDataList = UserDataStore.getUserRepository(context).affectDao.getAllAffectData()
        try {
            affectDataList.forEach { element ->
                if (element.transferred == false) {
                    Log.i("AddQuestionData", element.transferred.toString())
                    Log.i("AddQuestionData", element.id.toString())
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
                    Log.i("AddQuestionData", affect + " " + questionid + " " + time)

                    scope.launch {

                        Log.i("AddQuestionData", questionData.questionid)
                        UserDataStore.getUserRepository(context).questionDao.upsertQuestionData(questionData)
                        UserDataStore.getUserRepository(context).affectDao.markAsTransferred(element.id)
                    }
                } else {
                    Log.i("AddQuestionData", element.id.toString())
                }
            }
        }catch (e: Exception){
            Log.e("AddQuestionData", "${e.printStackTrace()}")
        }
    }

    private fun connectApi(){
        val timeoutSeconds = 30// Set your desired timeout value in seconds
        val client = OkHttpClient.Builder()
            .readTimeout(timeoutSeconds.toLong(), TimeUnit.SECONDS)
            .writeTimeout(timeoutSeconds.toLong(), TimeUnit.SECONDS)
            .build()
        Log.i("SendData", "Testing Timeout fix")
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
                    Log.i("SendData", "Test")

                    if (tokenResponse.isSuccessful) {
                        val token = "Token " + tokenResponse.body()?.getAsJsonPrimitive("token")?.asString
                        Log.i("SendData", token)

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
                            Log.i("SendData", postResponse.toString())
                            Log.i("SendData", "${postResponse.code()}")
                            Log.i("SendData", postResponse.body().toString())

                        } else {
                            Log.w("SendData", "${postResponse.code()}")
                            //Log.i("StoredDataApi",json.getStoredDataAsJsonObject().toString())
                        }
                    } else {
                        Log.w("SendData", "${tokenResponse.code()}")
                    }

                } catch (e: Exception) {
                    Log.e("SendData", "${e.printStackTrace()}")
                }
            } catch (e: java.lang.Exception) {
                Log.e("SendData", "${e.printStackTrace()}")
            }
        }
        thread.start()
        thread.join()
    }
}