package com.plcoding.wearosstopwatch.presentation.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.plcoding.wearosstopwatch.presentation.database.entities.HeartrateData
import kotlinx.coroutines.flow.Flow

@Dao
interface HeartrateDao {
    @Upsert
    suspend fun upsertHeartrateData(heartrateData: HeartrateData)

    @Delete
    suspend fun deleteHeartrateData(heartrateData: HeartrateData)

    @Query("SELECT * FROM heartrateData ORDER BY time ASC")
    fun getByTimeOrdered(): Flow<List<HeartrateData>>

    @Query("SELECT * FROM heartrateData ORDER BY sync ASC")
    fun getBySyncOrdered(): Flow<List<HeartrateData>>

    @Query("SELECT * FROM heartrateData ORDER BY time DESC LIMIT 1")
    suspend fun getLatestHeartrateData(): List<HeartrateData>
}