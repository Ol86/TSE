package com.plcoding.wearosstopwatch.presentation.database.daos

import androidx.room.Dao
import androidx.room.Insert
import com.plcoding.wearosstopwatch.presentation.database.entities.AffectData

@Dao
interface AffectDao {

    @Insert
    suspend fun insert(affect: AffectData): Long
}