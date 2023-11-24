package com.plcoding.wearosstopwatch.presentation.database

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import com.plcoding.wearosstopwatch.presentation.database.daos.*
import com.plcoding.wearosstopwatch.presentation.database.entities.*

@Database(version = 4,
    entities = [
        SessionData::class,
        ActiveMeasurement::class,
        InteractionMeasurement::class,
        NonHealthMeasurement::class,
        NotificationData::class,
        PassiveMeasurement::class,
        StudyData::class,
        AffectData::class
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
}
