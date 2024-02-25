package com.plcoding.wearosstopwatch.presentation.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.plcoding.wearosstopwatch.presentation.database.entities.PpgRedData
import kotlinx.coroutines.flow.Flow

@Dao
interface PpgRedDao {
    @Upsert
    suspend fun upsertPpgRedData(ppgRedData: PpgRedData)

    @Delete
    suspend fun deletePpgRedData(ppgRedData: PpgRedData)

    @Query("SELECT * FROM ppgRedData ORDER BY time ASC")
    fun getByTimeOrdered(): Flow<List<PpgRedData>>

    @Query("SELECT * FROM ppgRedData ORDER BY sync ASC")
    fun getBySyncOrdered(): Flow<List<PpgRedData>>

    @Query("SELECT * FROM ppgRedData ORDER BY time DESC LIMIT 1")
    fun getLatestPpgRedData(): List<PpgRedData>

    @Query("SELECT * FROM ppgRedData WHERE sync = 0 ORDER BY id ASC")
    fun getAllLatestPpgRedData(): List<PpgRedData>

    @Query("UPDATE ppgreddata SET sync = :s WHERE id = :id")
    suspend fun markAsSynced(s: String, id: Long)
}