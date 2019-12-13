package com.example.gamebook.data

class Game (
    val title : String,
    val description : String,
    val scenes : Array<Scene>,
    val currentSceneIndex: Int = 0
) {
    fun getCurrentActivity() : Class<*> {
        return getCurrentScene().getActivityType()
    }

    fun getCurrentScene() : Scene {
        return scenes[currentSceneIndex]
    }
}