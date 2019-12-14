package com.example.gamebook.data

import com.example.gamebook.GameOverActivity

class GameOverScene (
    title : String,
    text : String
) : Scene(title, text) {
    override fun getActivityType(): Class<*> {
        return GameOverActivity::class.java
    }
}