package com.plcoding.wearosstopwatch.presentation.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.plcoding.wearosstopwatch.presentation.database.entities.AffectDataTransferStatus

@Dao
interface AffectDataTransferStatusDao{
    @Insert
    suspend fun insert(status: AffectDataTransferStatus): Long

    @Query("SELECT * FROM affectdatatransferstatus WHERE transferred = false")
    suspend fun getPendingStatus(): List<AffectDataTransferStatus>

    @Query("UPDATE affectdatatransferstatus SET transferred = true WHERE id = :id")
    suspend fun markAsTransferred(id: Long)

    @Query("SELECT * FROM affectdatatransferstatus ORDER BY affectDataId DESC LIMIT 1")
    fun getLatestAffectDataTransferStatus(): AffectDataTransferStatus

    @Query("SELECT * FROM affectdatatransferstatus ORDER BY id ASC")
    fun getAllLatestAffectDataTransferStatus(): List<AffectDataTransferStatus>
}
