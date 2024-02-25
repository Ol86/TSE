package com.plcoding.wearosstopwatch.presentation
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay

class MyWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        try {
            for (i in 1..10) {
                Log.i("MyWorker", "Log-Nachricht Nr. $i")
                delay(10000) // 10 Sekunden Verzögerung
            }
            return Result.success()
        } catch (e: Exception) {
            Log.e("MyWorker", "Fehler bei der Ausführung der Arbeit: ${e.message}", e)
            return Result.failure()
        }
    }
}

/*
    fun convertToJsonObject(map: Map<String, Any>): JSONObject {
        val jsonObject = JSONObject()
        for ((key, value) in map) {
            when (value) {
                is Map<*, *> -> {
                    jsonObject.put(key, convertToJsonObject(value as Map<String, Any>))
                }
                is List<*> -> {
                    val jsonArray = JSONArray()
                    for (item in value) {
                        if (item is Map<*, *>) {
                            jsonArray.put(convertToJsonObject(item as Map<String, Any>))
                        } else {
                            jsonArray.put(item)
                        }
                    }
                    jsonObject.put(key, jsonArray)
                }
                else -> {
                    jsonObject.put(key, value)
                }
            }
        }
        return jsonObject
    }
 */
