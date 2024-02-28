package com.plcoding.wearosstopwatch.presentation.database

import android.se.omapi.Session
import android.util.Log
import com.plcoding.wearosstopwatch.presentation.database.daos.SessionDao
import com.plcoding.wearosstopwatch.presentation.database.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.time.LocalDateTime

class UserRepository(db: UserDatabase) {

    //UserDataStore.getUserRepository(context)
    //, private val context: Context

    private var sessionid = "1"
    val activeDao = db.getActiveMeasurementDao()
    val interactionDao = db.getInteractionMeasurementDao()
    val passiveDao = db.getPassiveMeasurementDao()
    val studyDao = db.getStudyDataDao()
    val affectDao = db.getAffectDao()
    val notificationDao = db.getNotificationDao()
    val sessionDao = db.getSessionDataDao()
    val questionDao = db.getQuestionDao()
    val accelerometerDao = db.getAccelerometerDao()
    val ecgDao =  db.getEcgDao()
    val heartrateDao = db.getHeartrateDao()
    val ppgGreenDao = db.getPpgGreenDao()
    val ppgIRDao = db.getPpgIRDao()
    val ppgRedDao = db.getPpgRedDao()
    val spo2Dao = db.getSpo2Dao()
    val sessionIDDao = db.getSessionIDDao()

    /*fun handleHeartRateDataList(
        list: MutableList<DataPointContainer>,
        scope: CoroutineScope,
        onFinished: (entity: MutableList<ActiveMeasurement>) -> Unit
    ) {
        var resultsToEntity: MutableList<ActiveMeasurement> = mutableListOf()
        list.forEach { dataPointContainer ->
            dataPointContainer.sampleDataPoints.forEach {
                val heartRate: Double = it.value as Double
                val heartRateAccuracy = it.accuracy.toString()
                //TODO THESE TWO VALUES ARE FOR TESTING: MAKE SURE TO GET THE PROPER TIME AND ID
                val currentTime = LocalDateTime.now().toString()
                val notificationId = 0L
                resultsToEntity
                    .add(ActiveMeasurement(0, notificationId, currentTime, heartRate, heartRateAccuracy))
            }
        }
        insertActiveMeasurementList(scope, resultsToEntity) {
            Log.v("ActiveMeasurementCall", "Sent Entities to db")
        }
        onFinished(resultsToEntity)
    }*/

    suspend fun getLatestDataAsJson(): String {
        sessionid = sessionIDDao.getActiveSessionID().session
        return withContext(Dispatchers.IO) {
            val questionData = questionDao.getAllQuestionData()
            questionData.forEach { element ->
                questionDao.markAsSynced("1", element.id)
            }
            val ecgData = ecgDao.getLatestEcgData()
            ecgData.forEach { element ->
                ecgDao.markAsSynced("1", element.id)
            }
            val heartrateData = heartrateDao.getAllLatestHeartrateData()
            heartrateData.forEach { element ->
                heartrateDao.markAsSynced("1", element.id)
            }
            val spo2Data = spo2Dao.getLatestSpo2Data()
            spo2Data.forEach { element ->
                spo2Dao.markAsSynced("1", element.id)
            }
            val accelerometerData = accelerometerDao.getAllLatestAccelerometerData()
            accelerometerData.forEach { element ->
                accelerometerDao.markAsSynced("1", element.id)
            }
            val ppgIRData = ppgIRDao.getAllLatestPpgIRData()
            ppgIRData.forEach { element ->
                ppgIRDao.markAsSynced("1", element.id)
            }
            val ppgRedData = ppgRedDao.getAllLatestPpgRedData()
            ppgRedData.forEach { element ->
                ppgRedDao.markAsSynced("1", element.id)
            }
            val ppgGreenData = ppgGreenDao.getAllLatestPpgGreenData()
            ppgGreenData.forEach { element ->
                ppgGreenDao.markAsSynced("1", element.id)
            }



            val dataMap = mutableMapOf<String, List<Map<String, String>>>()
            dataMap["ecg"] = ecgData.map { it.toJsonMap() }
            dataMap["heartrate"] = heartrateData.map { it.toJsonMap() }
            dataMap["spo2"] = spo2Data.map { it.toJsonMap() }
            dataMap["accelerometer"] = accelerometerData.map { it.toJsonMap() }
            dataMap["ppgir"] = ppgIRData.map { it.toJsonMap() }
            dataMap["ppgred"] = ppgRedData.map { it.toJsonMap() }
            dataMap["ppggreen"] = ppgGreenData.map { it.toJsonMap() }


            val jsonMap = mutableMapOf<String, Any>()
            jsonMap["session"] = sessionid
            jsonMap["questions"] = questionData.map { it.toJsonMap() }

            // Add the 'data' object with other data types if necessary
            if (dataMap.isNotEmpty()) {
                Log.i("DebuggingA012", "is not empty")
                jsonMap["data"] = dataMap
            }

            JSONObject(jsonMap as Map<*, *>).toString()
        }
    }

    fun getActiveSession(
        scope: CoroutineScope,
        onFinished: (entity: SessionData) -> Unit,
        onError: (e: Exception) -> Unit
    ) {
        scope.launch(Dispatchers.IO) {
            try {
                val result = sessionDao.getActiveSession()
                withContext(Dispatchers.Main) {
                    onFinished(result)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(e)
                }
            }
        }
    }


    fun insertActiveMeasurementList(
        scope: CoroutineScope,
        entities: MutableList<ActiveMeasurement>,
        onFinished: (entity: MutableList<ActiveMeasurement>) -> Unit
    ) {
        scope.launch(Dispatchers.IO) {
            val listOfIds = activeDao.insertAll(entities)
            entities.forEachIndexed() {index, element ->
                element.id = listOfIds[index]
            }
            Log.v("ActiveMeasurementRepo", "Inserted following data into db: $entities")
            withContext(Dispatchers.Main) {
                onFinished(entities)
            }
        }
    }

    fun insertInteractionMeasurement(
        scope: CoroutineScope,
        entity: InteractionMeasurement,
        onFinished: (entity: InteractionMeasurement) -> Unit
    ) {
        scope.launch(Dispatchers.IO) {
            entity.id = interactionDao.insert(entity)
            Log.v("InteractionMeasurementRepo", "Inserted following data into db: $entity")
            withContext(Dispatchers.Main) {
                onFinished(entity)
            }
        }
    }

    fun insertAffect(
        scope: CoroutineScope,
        entity: AffectData,
        onFinished: (entity: AffectData) -> Unit
    ) {
        scope.launch(Dispatchers.IO) {
            entity.id = affectDao.insert(entity)
            Log.v("AfffectDataRepo", "Inserted following data into db: $entity")
            withContext(Dispatchers.Main) {
                onFinished(entity)
            }
        }
    }

    fun insertStartTime(
        scope: CoroutineScope,
        entity: StudyData,
        onFinished: (entity: StudyData) -> Unit
    ) {
        scope.launch(Dispatchers.IO) {
            entity.id = studyDao.insert(entity)
            Log.v("StudyDataRepo", "Inserted following data into db: $entity")
            withContext(Dispatchers.Main) {
                onFinished(entity)
            }
        }
    }

    fun insertNotificationTime(
        scope: CoroutineScope,
        entity: NotificationData,
        onFinished: (entity: NotificationData) -> Unit
    ) {
        scope.launch(Dispatchers.IO) {
            entity.id = notificationDao.insert(entity)
            Log.v("NotificationDataRepo", "Inserted following data into db: $entity")
            withContext(Dispatchers.Main) {
                onFinished(entity)
            }
        }
    }

    fun insertSession(
        scope: CoroutineScope,
        entity: SessionData,
        onFinished: (entity: SessionData) -> Unit
    ) {
        scope.launch(Dispatchers.IO) {
            entity.id = sessionDao.insert(entity)
            withContext(Dispatchers.Main) {
                onFinished(entity)
            }
        }
    }

    fun updateSession(
        scope: CoroutineScope,
        entity: SessionData,
        onFinished: (entity: SessionData) -> Unit
    ) {
        scope.launch(Dispatchers.IO) {
            entity.id = sessionDao.insert(entity)
            withContext(Dispatchers.Main) {
                onFinished(entity)
            }
        }
    }

    fun insertAccelerometerData(
        scope: CoroutineScope,
        entity: AccelerometerData,
        onFinished: (entity: AccelerometerData) -> Unit
    ){
        scope.launch(Dispatchers.IO){
            entity.id = accelerometerDao.upsertAccelerometerData(entity)
            withContext(Dispatchers.Main){
                onFinished(entity)
            }
        }
    }

    fun insertQuestionData(
        scope: CoroutineScope,
        entity: QuestionData,
        onFinished: (entity: QuestionData) -> Unit
    ){
        scope.launch(Dispatchers.IO){
            Log.i("DebuggingA00","In UserRep")
            entity.id = questionDao.insert(entity)
            withContext(Dispatchers.Main){
                onFinished(entity)
            }
        }
    }

}