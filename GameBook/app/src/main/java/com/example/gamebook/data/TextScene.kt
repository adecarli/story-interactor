package com.example.gamebook.data

import com.example.gamebook.TextActivity

class TextScene (
    title : String,
    text : String,
    val okLink : Int
) : Scene(title, text) {
    override fun getActivityType(): Class<*> {
        return TextActivity::class.java
    }
}
