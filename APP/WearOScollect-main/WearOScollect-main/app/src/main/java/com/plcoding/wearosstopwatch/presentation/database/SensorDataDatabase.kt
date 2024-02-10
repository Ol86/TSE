package com.plcoding.wearosstopwatch.presentation.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.plcoding.wearosstopwatch.presentation.database.daos.AccelerometerDao
import com.plcoding.wearosstopwatch.presentation.database.daos.EcgDao
import com.plcoding.wearosstopwatch.presentation.database.daos.HeartrateDao
import com.plcoding.wearosstopwatch.presentation.database.daos.PpgGreenDao
import com.plcoding.wearosstopwatch.presentation.database.daos.PpgIRDao
import com.plcoding.wearosstopwatch.presentation.database.daos.PpgRedDao
import com.plcoding.wearosstopwatch.presentation.database.daos.Spo2Dao
import com.plcoding.wearosstopwatch.presentation.database.entities.AccelerometerData
import com.plcoding.wearosstopwatch.presentation.database.entities.EcgData
import com.plcoding.wearosstopwatch.presentation.database.entities.HeartrateData
import com.plcoding.wearosstopwatch.presentation.database.entities.PpgGreenData
import com.plcoding.wearosstopwatch.presentation.database.entities.PpgIRData
import com.plcoding.wearosstopwatch.presentation.database.entities.PpgRedData
import com.plcoding.wearosstopwatch.presentation.database.entities.Spo2Data
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

@Database(entities = [AccelerometerData::class, EcgData::class, HeartrateData::class, PpgGreenData::class, PpgIRData::class, PpgRedData::class, Spo2Data::class],
    version = 1)
abstract class SensorDataDatabase: RoomDatabase() {
    abstract val ecgDao: EcgDao
    abstract val accelerometerDao: AccelerometerDao
    abstract val heartrateDao: HeartrateDao
    abstract val ppgGreenDao: PpgGreenDao
    abstract val ppgIRDao: PpgIRDao
    abstract val ppgRedDao: PpgRedDao
    abstract val spo2Dao: Spo2Dao

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
            val ecgData = ecgDao.getLatestEcgData()
            val accelerometerData = accelerometerDao.getLatestAccelerometerData()
            val heartrateData = heartrateDao.getLatestHeartrateData()
            val ppgGreenData = ppgGreenDao.getLatestPpgGreenData()
            val ppgIRData = ppgIRDao.getLatestPpgIRData()
            val ppgRedData = ppgRedDao.getLatestPpgRedData()
            val spo2Data = spo2Dao.getLatestSpo2Data()

            val jsonMap = mutableMapOf<String, List<Map<String, String>>>()

            jsonMap["ecg"] = ecgData.map { it.toJsonMap() }
            jsonMap["accelerometer"] = accelerometerData.map { it.toJsonMap() }
            jsonMap["heartrate"] = heartrateData.map { it.toJsonMap() }
            jsonMap["ppggreen"] = ppgGreenData.map { it.toJsonMap() }
            jsonMap["ppgir"] = ppgIRData.map { it.toJsonMap() }
            jsonMap["ppgred"] = ppgRedData.map { it.toJsonMap() }
            jsonMap["spo2"] = spo2Data.map { it.toJsonMap() }

            val jsonObject = convertToJsonObject(jsonMap)
            jsonObject.toString()
        }
    }
}