package com.plcoding.wearosstopwatch.presentation.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.plcoding.wearosstopwatch.presentation.database.entities.AffectData
import com.plcoding.wearosstopwatch.presentation.database.entities.NotificationData

@Dao
interface AffectDao {

    @Insert
    suspend fun insert(affect: AffectData): Long

    @Query("UPDATE affectdata SET transferred = true WHERE id = :id")
    suspend fun markAsTransferred(id: Long)

    @Query("SELECT * FROM affectdata ORDER BY id DESC LIMIT 1")
    fun getAffectData(): AffectData

    @Query("SELECT * FROM affectdata ORDER BY id ASC")
    fun getAllAffectData(): List<AffectData>

    @Query("SELECT * FROM affectdata WHERE id = :id")
    fun getAffectDataByID(id: Long): AffectData


}