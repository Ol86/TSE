package com.plcoding.wearosstopwatch.presentation.database.daos

import androidx.room.Dao
import androidx.room.Insert
import com.plcoding.wearosstopwatch.presentation.database.entities.StudyData

@Dao
interface StudyDao {

    @Insert
    suspend fun insert(study: StudyData): Long
}