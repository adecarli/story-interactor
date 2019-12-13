package com.example.gamebook.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "serialized_game")
data class SerializedGame (
    @PrimaryKey(autoGenerate = true) val uid : Long,
    @ColumnInfo(name = "json") val json : String,
    @ColumnInfo(name = "last_played") val lastPlayed : String
)