package com.example.gamebook.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "serialized_game")
data class SerializedGame (
    @PrimaryKey(autoGenerate = true) val uid : Long,
    @ColumnInfo(name = "json") var json : String,
    @ColumnInfo(name = "last_played") var lastPlayed : String,
    @ColumnInfo(name = "notes") var notes : String = ""
)