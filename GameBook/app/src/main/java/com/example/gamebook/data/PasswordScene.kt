package com.example.gamebook.data

import com.example.gamebook.PasswordActivity

class PasswordScene (
    title : String,
    text : String,
    val password : String,
    val okLink : Int,
    val errorLink : Int
) : Scene(title, text) {
    override fun getActivityType(): Class<*> {
        return PasswordActivity::class.java
    }
}