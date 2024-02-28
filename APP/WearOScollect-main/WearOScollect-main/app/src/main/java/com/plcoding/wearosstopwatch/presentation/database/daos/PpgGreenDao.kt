package com.plcoding.wearosstopwatch.presentation.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.plcoding.wearosstopwatch.presentation.database.entities.PpgGreenData
import kotlinx.coroutines.flow.Flow

@Dao
interface PpgGreenDao {
    @Upsert
    suspend fun upsertPpgGreenData(ppgGreenData: PpgGreenData)

    @Delete
    suspend fun deleteData(ppgGreenData: PpgGreenData)

    @Query("SELECT * FROM ppgGreenData ORDER BY time ASC")
    fun getByTimeOrdered(): Flow<List<PpgGreenData>>

    @Query("SELECT * FROM ppgGreenData ORDER BY sync ASC")
    fun getBySyncOrdered(): Flow<List<PpgGreenData>>

    @Query("SELECT * FROM ppgGreenData ORDER BY time DESC LIMIT 1")
    fun getLatestPpgGreenData(): List<PpgGreenData>

    @Query("SELECT * FROM ppgGreenData WHERE sync = 0 ORDER BY id ASC")
    fun getAllLatestPpgGreenData(): List<PpgGreenData>

    @Query("UPDATE ppggreendata SET sync = :s WHERE id = :id")
    suspend fun markAsSynced(s: String, id: Long)
}