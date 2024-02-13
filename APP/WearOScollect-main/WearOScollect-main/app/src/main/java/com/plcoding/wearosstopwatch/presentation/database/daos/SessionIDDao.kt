package com.plcoding.wearosstopwatch.presentation.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.plcoding.wearosstopwatch.presentation.database.entities.SessionData
import com.plcoding.wearosstopwatch.presentation.database.entities.SessionIDData
@Dao
interface SessionIDDao {
    @Upsert
    suspend fun upsertSessionIDData(sessionIDData: SessionIDData)

    @Query("SELECT * FROM sessionIDData ORDER BY session DESC LIMIT 1")
    suspend fun getActiveSessionID(): SessionIDData
}