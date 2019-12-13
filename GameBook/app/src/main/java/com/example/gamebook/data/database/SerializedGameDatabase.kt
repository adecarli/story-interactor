package com.example.gamebook.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SerializedGame::class], version = 1)
abstract class SerializedGameDatabase : RoomDatabase() {
    abstract fun serializedGameDao(): SerializedGameDao

    companion object {
        private var instance: SerializedGameDatabase? = null
        private val sLock = Any()

        fun getInstance(context: Context): SerializedGameDatabase {
            synchronized (sLock) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SerializedGameDatabase::class.java, "SerializedGameDatabase.db"
                    ).build()
                }
                return instance!!
            }
        }
    }
}