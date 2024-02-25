import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.plcoding.wearosstopwatch.presentation.PostApiService
import com.plcoding.wearosstopwatch.presentation.api.ApiService
import com.plcoding.wearosstopwatch.presentation.database.UserDataStore
import com.plcoding.wearosstopwatch.presentation.database.entities.AffectDataTransferStatus
import com.plcoding.wearosstopwatch.presentation.database.entities.NotificationDataTransferStatus
import com.plcoding.wearosstopwatch.presentation.database.entities.QuestionData
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlin.concurrent.timerTask

class DataSender( coroutineScope: LifecycleCoroutineScope,private val context: Context) {
/*


    private var timer: Timer? = null
    private val intervalMillis = 10000 // Intervall in Millisekunden (hier 10 Sekunden)
    private val scope = coroutineScope
    private var dbData: String = ""



    fun startSending() {
        timer = Timer()
        timer?.scheduleAtFixedRate(timerTask {
            connectApi()
        }, 0, intervalMillis.toLong())
    }

    fun stopSending() {
        timer?.cancel()
    }

    private fun stringTOJSON(dbData: String): JSONObject {
        return JSONObject(dbData)
    }

    /*    private fun sendData() {
            val thread = Thread {
                try {
                    scope.launch {
                        dbData = database.getLatestDataAsJson()
                        //Log.i("uiuiui6",db.getLatestDataAsJson())
                    }
                    val json = this.stringTOJSON(dbData)
                    val token = getToken() // Hier muss die Methode implementiert werden, um das Token zu erhalten

                    val sendResponse = apiService.testPost(json, token).execute()

                    if (sendResponse.isSuccessful) {
                        Log.i("SendData", "Data sent successfully")
                    } else {
                        println("Error: ${sendResponse.code()}")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            thread.start()
        }*/

    private fun addQuestionData(){
        val newAffectData = UserDataStore.getUserRepository(context).affectDao.getAllAffectData()
        val newNotificationData = UserDataStore.getUserRepository(context).notificationDao.getAllNotificationData()
        //val questionData = QuestionData(time, affect, questionID,"0")
        val maxAffectID: Long
        val maxNotID: Long
        if(db.affectDataTransferStatusDao.getLatestAffectDataTransferStatus() != null) {
            maxAffectID =
                db.affectDataTransferStatusDao.getLatestAffectDataTransferStatus().affectDataId
        } else {
            maxAffectID = 0
        }
        if (db.notificationDataTransferStatusDao.getLatestNotificationDataTransferStatus() != null) {
            maxNotID = db.notificationDataTransferStatusDao.getLatestNotificationDataTransferStatus().notificationDataId
        } else {
            maxNotID = 0
        }
        newAffectData.forEach {element ->
            Log.i("DebuggingA33.1", maxAffectID.toString())
            Log.i("DebuggingA33.2", element.id.toString())
            if(element.id > maxAffectID) {
                Log.i("DebuggingA33.3", maxAffectID.toString())
                Log.i("DebuggingA33.4", element.id.toString())
                val a = AffectDataTransferStatus(element.id, false)
                scope.launch {
                    db.affectDataTransferStatusDao.insert(a)
                }
            } else {
                Log.i("DebuggingA33.5", maxAffectID.toString())
                Log.i("DebuggingA33.6", element.id.toString())
            }
        }
        newNotificationData.forEach {element ->
            Log.i("DebuggingA34.1", maxNotID.toString())
            Log.i("DebuggingA34.2", element.id.toString())
            if(element.id > maxNotID) {
                Log.i("DebuggingA34.3", maxNotID.toString())
                Log.i("DebuggingA34.4", element.id.toString())
                val a = NotificationDataTransferStatus(element.id, false)
                scope.launch {
                    db.notificationDataTransferStatusDao.insert(a)
                }
            } else {
                Log.i("DebuggingA34.5", maxNotID.toString())
                Log.i("DebuggingA34.6", element.id.toString() + "; " + element.time)
            }
        }
        /*
        val transferredAffect = db.affectDataTransferStatusDao.getAllLatestAffectDataTransferStatus()
        val transferredNotification = db.notificationDataTransferStatusDao.getAllLatestNotificationDataTransferStatus()
        transferredAffect.forEach{element->
            if (element.id > maxAffectID){
                val time: String
                val affect: String
                val questionid: String
            }
        }*/
    }
    private fun dbDataTOJSON(): JsonObject {
        try {

            val affect = UserDataStore.getUserRepository(context).affectDao.getAffectData().affect
            val time =
                UserDataStore.getUserRepository(context).notificationDao.getNotificationData().time
            val questionID =
                UserDataStore.getUserRepository(context).affectDao.getAffectData().notification_id.toString()
            val questionData = QuestionData(time, affect, questionID, "0")
            this.addQuestionData()
            scope.launch {
                dbData = db.getLatestDataAsJson()
            }
        } catch (e: Exception){

        }
        scope.launch {
            dbData = db.getLatestDataAsJson()
        }
        Log.i("DebuggingA1", dbData)
        val jsonObject: JsonObject = JsonParser().parse(dbData)
            .getAsJsonObject()
        Log.i("DebuggingA1", jsonObject.toString())
        return jsonObject
    }

    /*    private fun getToken(): String {
            var token: String = ""


            val response = apiService.getToken(
                JsonObject().apply {
                addProperty("username", "Watch1")
                addProperty("password", "tse-KIT-2023")
            }).execute()

            if (response.isSuccessful) {
                token = "Token " + response.body()?.getAsJsonPrimitive("token")?.asString
                println(token)
            } else {
                println("Error: ${response.code()}")
            }

            return token
        }*/

    private fun connectApi(){
        val thread = Thread {
            try {
                Log.i("APImessage", "Connect")
                val retrofit = Retrofit.Builder()
                    .baseUrl("http://193.196.36.62:9000/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val postRetrofit = Retrofit.Builder()
                    .baseUrl("http://193.196.36.62/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val apiService: ApiService = retrofit.create(ApiService::class.java)
                val postApiService = postRetrofit.create(PostApiService::class.java)

                try {
                    val tokenResponse = apiService.getToken(

                        JsonObject().apply {
                            addProperty("username", "Watch1")
                            addProperty("password", "tse-KIT-2023")
                        }
                    ).execute()





                    val a =
                        """
     {
        "data": {
            "accelerometer": [
                {
                    "time": "1693171872340",
                    "x": "408",
                    "y": "-437",
                    "z": "4059"
                }
            ],
            "ecg": [],
            "heartrate": [
                {
                    "hr": "70",
                    "hr_status": "75",
                    "ibi": "32",
                    "ibi_status": "0",
                    "time": "1693171881248"
                }
            ],
            "ppggreen": [
                {
                    "ppggreen": "853543",
                    "time": "1693171872340"
                }
            ],
            "ppgir": [
                {
                    "ppgir": "5952436",
                    "time": "1693171882570"
                }
            ],
            "ppgred": [
                {
                    "ppgred": "5977263",
                    "time": "1693171882570"
                }
            ],
            "spo2": [
                {
                    "heartRate": "0",
                    "spo2": "0",
                    "status": "-6",
                    "time": "1693171882594"
                }
            ]
        },
        "session": 1
    }
    """
                    Log.i("sendDataApi0", "Test")

                    val jsonObject: JsonObject = JsonParser().parse(a)
                        .getAsJsonObject()
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

    }*/
}
/*private fun sendData(){
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
                    Log.i("ServerResponseApi", token)

                    val postResponse = apiService.testPost(
                        this.dbDataTOJSON(),
                        token
                    ).execute()

                    if (postResponse.isSuccessful) {
                        //println(postResponse.body())
                        Log.i("SendData", "Data sent successfully")
                    } else {
                        println("Error: ${postResponse.code()}")
                        Log.i("ServerResponseApi", postResponse.toString())
                        Log.i("ServerResponseApi", "${postResponse.code()}")
                    }
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

}*/
