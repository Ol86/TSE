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
}