package com.plcoding.wearosstopwatch.presentation.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.plcoding.wearosstopwatch.presentation.database.entities.PpgIRData
import kotlinx.coroutines.flow.Flow

@Dao
interface PpgIRDao {
    @Upsert
    suspend fun upsertPpgIRData(ppgIRData: PpgIRData)

    @Delete
    suspend fun deletePpgIRData(ppgIRData: PpgIRData)

    @Query("SELECT * FROM ppgIRData ORDER BY time ASC")
    fun getByTimeOrdered(): Flow<List<PpgIRData>>

    @Query("SELECT * FROM ppgIRData ORDER BY sync ASC")
    fun getBySyncOrdered(): Flow<List<PpgIRData>>

    @Query("SELECT * FROM ppgIRData ORDER BY time DESC LIMIT 1")
    fun getLatestPpgIRData(): List<PpgIRData>

    @Query("SELECT * FROM ppgIRData WHERE sync = 0 ORDER BY id ASC")
    fun getAllLatestPpgIRData(): List<PpgIRData>

    @Query("UPDATE ppgirdata SET sync = :s WHERE id = :id")
    suspend fun markAsSynced(s: String, id: Long)
}