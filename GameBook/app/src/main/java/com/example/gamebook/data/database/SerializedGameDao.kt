package com.example.gamebook.data.database

import androidx.room.*

@Dao
interface SerializedGameDao {

    @Query("SELECT * FROM serialized_game")
    fun getAll(): List<SerializedGame>

    @Query("SELECT * FROM serialized_game WHERE uid = :id")
    fun get(id: Long): SerializedGame?

    @Insert
    fun insert(serializedGame: SerializedGame) : Long

    @Delete
    fun delete(serializedGame: SerializedGame)

    @Update
    fun update(serializedGame: SerializedGame)

}