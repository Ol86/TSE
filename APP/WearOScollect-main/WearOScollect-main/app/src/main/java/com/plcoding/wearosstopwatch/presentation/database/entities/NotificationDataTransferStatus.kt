package com.plcoding.wearosstopwatch.presentation.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NotificationDataTransferStatus(
    var notificationDataId: Long,
    var transferred: Boolean = false,
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
)
