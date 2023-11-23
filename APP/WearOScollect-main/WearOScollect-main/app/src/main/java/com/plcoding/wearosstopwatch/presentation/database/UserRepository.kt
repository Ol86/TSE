package com.plcoding.wearosstopwatch.presentation.database

import android.se.omapi.Session
import android.util.Log
import com.plcoding.wearosstopwatch.presentation.database.daos.SessionDao
import com.plcoding.wearosstopwatch.presentation.database.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class UserRepository(db: UserDatabase) {

    val activeDao = db.getActiveMeasurementDao()
    val interactionDao = db.getInteractionMeasurementDao()
    val passiveDao = db.getPassiveMeasurementDao()
    val studyDao = db.getStudyDataDao()
    val affectDao = db.getAffectDao()
    val notificationDao = db.getNotificationDao()
    val sessionDao = db.getSessionDataDao()

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

}