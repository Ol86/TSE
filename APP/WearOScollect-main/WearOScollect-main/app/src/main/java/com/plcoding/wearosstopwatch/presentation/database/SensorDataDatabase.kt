package com.plcoding.wearosstopwatch.presentation.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.plcoding.wearosstopwatch.presentation.database.daos.AccelerometerDao
import com.plcoding.wearosstopwatch.presentation.database.daos.EcgDao
import com.plcoding.wearosstopwatch.presentation.database.daos.HeartrateDao
import com.plcoding.wearosstopwatch.presentation.database.daos.PpgGreenDao
import com.plcoding.wearosstopwatch.presentation.database.daos.PpgIRDao
import com.plcoding.wearosstopwatch.presentation.database.daos.PpgRedDao
import com.plcoding.wearosstopwatch.presentation.database.daos.QuestionDao
import com.plcoding.wearosstopwatch.presentation.database.daos.Spo2Dao
import com.plcoding.wearosstopwatch.presentation.database.entities.AccelerometerData
import com.plcoding.wearosstopwatch.presentation.database.entities.EcgData
import com.plcoding.wearosstopwatch.presentation.database.entities.HeartrateData
import com.plcoding.wearosstopwatch.presentation.database.entities.PpgGreenData
import com.plcoding.wearosstopwatch.presentation.database.entities.PpgIRData
import com.plcoding.wearosstopwatch.presentation.database.entities.PpgRedData
import com.plcoding.wearosstopwatch.presentation.database.entities.QuestionData
import com.plcoding.wearosstopwatch.presentation.database.entities.Spo2Data
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

@Database(entities = [EcgData::class, HeartrateData::class, Spo2Data::class, AccelerometerData::class, PpgIRData::class, PpgRedData::class, PpgGreenData::class, QuestionData::class],
    version = 3)
abstract class SensorDataDatabase: RoomDatabase() {
    abstract val ecgDao: EcgDao
    abstract val heartrateDao: HeartrateDao
    abstract val spo2Dao: Spo2Dao
    abstract val accelerometerDao: AccelerometerDao
    abstract val ppgIRDao: PpgIRDao
    abstract val ppgRedDao: PpgRedDao
    abstract val ppgGreenDao: PpgGreenDao
    abstract val questionDao: QuestionDao
    private var sessionid = 1

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

    suspend fun getLatestDataAsJson(): String {
        return withContext(Dispatchers.IO) {
            val questionData = questionDao.getLatestQuestionData()
            val ecgData = ecgDao.getLatestEcgData()
            val heartrateData = heartrateDao.getLatestHeartrateData()
            val spo2Data = spo2Dao.getLatestSpo2Data()
            val accelerometerData = accelerometerDao.getLatestAccelerometerData()
            val ppgIRData = ppgIRDao.getLatestPpgIRData()
            val ppgRedData = ppgRedDao.getLatestPpgRedData()
            val ppgGreenData = ppgGreenDao.getLatestPpgGreenData()



            val dataMap = mutableMapOf<String, List<Map<String, String>>>()
            dataMap["ecg"] = ecgData.map { it.toJsonMap() }
            dataMap["heartrate"] = heartrateData.map { it.toJsonMap() }
            dataMap["accelerometer"] = accelerometerData.map { it.toJsonMap() }
            dataMap["spo2"] = spo2Data.map { it.toJsonMap() }
            dataMap["ppgir"] = ppgIRData.map { it.toJsonMap() }
            dataMap["ppgred"] = ppgRedData.map { it.toJsonMap() }
            dataMap["ppggreen"] = ppgGreenData.map { it.toJsonMap() }

            /*val jsonObject = convertToJsonObject(jsonMap)
            val sessionJsonObject = JSONObject()
            sessionJsonObject.put("session", sessionid)
            sessionJsonObject.put("data", jsonObject)
            sessionJsonObject.toString()*/
            /*jsonMap["data"] = sensorDataMap

            // Convert the map to JSON object
            val jsonObject = JSONObject(jsonMap as Map<*, *>)
            jsonObject.toString()*/

            val jsonMap = mutableMapOf<String, Any>()
            jsonMap["session"] = sessionid
            jsonMap["questions"] = questionData.map { it.toJsonMap() }

            // Add the 'data' object with other data types if necessary
            if (dataMap.isNotEmpty()) {
                jsonMap["data"] = dataMap
            }

            JSONObject(jsonMap as Map<*, *>).toString()
        }
    }
}