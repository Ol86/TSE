package com.plcoding.wearosstopwatch.presentation.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.plcoding.wearosstopwatch.presentation.database.entities.QuestionData
import com.plcoding.wearosstopwatch.presentation.database.entities.SessionData
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    @Upsert
    suspend fun upsertQuestionData(questionData: QuestionData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(questionData: QuestionData): Long

    @Delete
    suspend fun deleteQuestionData(questionData: QuestionData)

    @Query("SELECT * FROM questionData ORDER BY time ASC")
    fun getByTimeOrdered(): Flow<List<QuestionData>>

    @Query("SELECT * FROM questionData ORDER BY sync ASC")
    fun getBySyncOrdered(): Flow<List<QuestionData>>

    @Query("SELECT * FROM questionData ORDER BY time DESC LIMIT 1")
    fun getLatestQuestionData(): List<QuestionData>

    @Query("SELECT * FROM questionData ORDER BY questionid DESC LIMIT 1")
    fun getActiveQuestionData(): QuestionData

    @Query("SELECT * FROM questionData WHERE sync = 0 ORDER BY questionid asc")
    fun getAllQuestionData(): List<QuestionData>

    @Query("SELECT * FROM questionData WHERE questionid = (SELECT MAX(questionid) FROM questionData) ORDER BY id ASC")
    fun getLatestQuestionDataOrderedById(): List<QuestionData>

    @Query("UPDATE questiondata SET sync = :s WHERE id = :id")
    suspend fun markAsSynced(s: String, id: Long)

}
