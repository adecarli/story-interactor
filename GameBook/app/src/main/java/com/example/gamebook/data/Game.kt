package com.example.gamebook.data

class Game (
    val title : String,
    val description : String,
    val scenes : Array<Scene>,
    val currentScene: Int = 0
)
