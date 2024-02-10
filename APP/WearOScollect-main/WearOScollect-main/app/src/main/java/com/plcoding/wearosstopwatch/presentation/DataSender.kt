import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import com.google.gson.JsonObject
import com.plcoding.wearosstopwatch.presentation.MainActivity
import com.plcoding.wearosstopwatch.presentation.api.ApiService
import com.plcoding.wearosstopwatch.presentation.database.SensorDataDatabase
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlin.concurrent.timerTask

class DataSender(private val database: SensorDataDatabase, coroutineScope: LifecycleCoroutineScope) {


    private val retrofit = Retrofit.Builder()
        .baseUrl("http://193.196.36.62:9000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val apiService: ApiService = retrofit.create(ApiService::class.java)
    private var timer: Timer? = null
    private val intervalMillis = 10000 // Intervall in Millisekunden (hier 10 Sekunden)
    private val scope = coroutineScope
    private var dbData: String = ""


    fun startSending() {
        timer = Timer()
        timer?.scheduleAtFixedRate(timerTask {
            sendData()
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

    private fun dbDataTOJSON(): JSONObject {
        scope.launch {
            dbData = database.getLatestDataAsJson()
        }
        val json = this.stringTOJSON(dbData)
        return json
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

    private fun sendData(){
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

                        val postResponse = apiService.testPost(
                            this.dbDataTOJSON(),
                            token
                        ).execute()

                        if (postResponse.isSuccessful) {
                            //println(postResponse.body())
                            Log.i("SendData", "Data sent successfully")
                        } else {
                            println("Error: ${postResponse.code()}")
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

    }
}