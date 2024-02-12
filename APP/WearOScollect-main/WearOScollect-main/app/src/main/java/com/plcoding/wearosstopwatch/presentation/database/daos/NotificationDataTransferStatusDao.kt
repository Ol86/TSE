package com.plcoding.wearosstopwatch.presentation.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.plcoding.wearosstopwatch.presentation.database.entities.AffectDataTransferStatus
import com.plcoding.wearosstopwatch.presentation.database.entities.NotificationDataTransferStatus

@Dao
interface NotificationDataTransferStatusDao {
    @Insert
    suspend fun insert(status: NotificationDataTransferStatus): Long

    @Query("SELECT * FROM notificationdatatransferstatus WHERE transferred = false")
    suspend fun getPendingStatus(): List<NotificationDataTransferStatus>

    @Query("UPDATE notificationdatatransferstatus SET transferred = true WHERE id = :id")
    suspend fun markAsTransferred(id: Long)

    @Query("SELECT * FROM notificationdatatransferstatus ORDER BY notificationDataId DESC LIMIT 1")
    fun getLatestNotificationDataTransferStatus(): NotificationDataTransferStatus

    @Query("SELECT * FROM notificationdatatransferstatus ORDER BY id ASC")
    fun getAllLatestNotificationDataTransferStatus(): List<NotificationDataTransferStatus>
}