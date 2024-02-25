package com.plcoding.wearosstopwatch.presentation.database

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import com.plcoding.wearosstopwatch.presentation.database.daos.*
import com.plcoding.wearosstopwatch.presentation.database.entities.*

@Database(version = 9,
    entities = [
        SessionData::class,
        ActiveMeasurement::class,
        InteractionMeasurement::class,
        NonHealthMeasurement::class,
        NotificationData::class,
        PassiveMeasurement::class,
        StudyData::class,
        AffectData::class,
        QuestionData::class,
        AccelerometerData::class,
        EcgData::class,
        HeartrateData::class,
        PpgGreenData::class,
        PpgIRData::class,
        PpgRedData::class,
        Spo2Data::class,
        SessionIDData::class
    ]
)
abstract class UserDatabase: RoomDatabase(){
    abstract fun getSessionDataDao(): SessionDao
    abstract fun getActiveMeasurementDao(): ActiveMeasurementDao
    abstract fun getInteractionMeasurementDao(): InteractionMeasurementDao
    abstract fun getPassiveMeasurementDao(): PassiveMeasurementDao
    abstract fun getStudyDataDao(): StudyDao
    abstract fun getAffectDao(): AffectDao
    abstract fun getNotificationDao(): NotificationDao
    abstract fun getQuestionDao(): QuestionDao
    abstract fun getAccelerometerDao(): AccelerometerDao
    abstract fun getEcgDao(): EcgDao
    abstract fun getHeartrateDao(): HeartrateDao
    abstract fun getPpgGreenDao(): PpgGreenDao
    abstract fun getPpgIRDao(): PpgIRDao
    abstract fun getPpgRedDao(): PpgRedDao
    abstract fun getSpo2Dao(): Spo2Dao
    abstract fun getSessionIDDao(): SessionIDDao
}
