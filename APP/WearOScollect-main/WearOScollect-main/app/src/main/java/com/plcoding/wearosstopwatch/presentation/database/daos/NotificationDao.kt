package com.plcoding.wearosstopwatch.presentation.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import com.plcoding.wearosstopwatch.presentation.database.entities.NotificationData

@Dao
interface NotificationDao {

    @Insert
    suspend fun insert(notification: NotificationData): Long

}