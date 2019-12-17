package com.example.gamebook.data

import com.example.gamebook.ChoiceActivity

class ChoiceScene (
    title : String,
    text: String,
    val choices : Array<Choice>
) : Scene(title, text) {
    override fun getActivityType(): Class<*> {
        return ChoiceActivity::class.java
    }
}
