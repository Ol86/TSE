package com.plcoding.wearosstopwatch.presentation.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

object UserDataStore {

    private var roomDb: UserDatabase? = null

    private fun getDB(context: Context) : UserDatabase {
        if (roomDb == null) {
            roomDb = Room.databaseBuilder(context,
            UserDatabase::class.java, "study.db")
                .fallbackToDestructiveMigration()
                .addCallback(object: RoomDatabase.Callback() {
                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        roomDb?.let{
                            val repo = UserRepository(it)
                        }
                    }
                })
                .build()
        }
        return roomDb!!
    }

    fun getUserRepository(context: Context) = UserRepository(getDB(context))

}