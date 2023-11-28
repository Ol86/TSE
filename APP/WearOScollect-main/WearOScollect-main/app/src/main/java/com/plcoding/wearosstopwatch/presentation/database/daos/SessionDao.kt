package com.plcoding.wearosstopwatch.presentation.database.daos

import androidx.room.*
import com.plcoding.wearosstopwatch.presentation.database.entities.SessionData

@Dao
interface SessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: SessionData): Long

    @Query("SELECT * FROM sessionData WHERE endTimeMillis=0 ORDER BY startTimeMillis DESC LIMIT 1")
    fun getActiveSession(): SessionData
}