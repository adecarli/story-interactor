package com.example.gamebook.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase



@Database(entities = [SerializedGame::class], version = 1)
abstract class ApplicationDatabase : RoomDatabase() {
    abstract fun serializedGameDao() : SerializedGameDao

    companion object {
        private var instance: ApplicationDatabase? = null
        private val sLock = Any()

        fun getInstance(context: Context): ApplicationDatabase {
            synchronized (sLock) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ApplicationDatabase::class.java, "ApplicationDatabase.db"
                    ).build()
                }
                return instance!!
            }
        }
    }
}