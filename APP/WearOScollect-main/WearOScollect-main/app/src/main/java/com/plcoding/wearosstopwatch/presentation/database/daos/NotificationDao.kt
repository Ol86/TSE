package com.plcoding.wearosstopwatch.presentation.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.plcoding.wearosstopwatch.presentation.database.entities.NotificationData

@Dao
interface NotificationDao {

    @Insert
    suspend fun insert(notification: NotificationData): Long

    @Query("SELECT * FROM notificationdata ORDER BY id DESC LIMIT 1")
    fun getNotificationData(): NotificationData

    @Query("SELECT * FROM notificationdata ORDER BY id ASC")
    fun getAllNotificationData(): List<NotificationData>

    @Query("SELECT * FROM notificationdata WHERE id = :id")
    fun getNotificationDataByID(id: Long): NotificationData

}