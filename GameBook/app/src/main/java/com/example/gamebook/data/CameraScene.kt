package com.example.gamebook.data

import com.example.gamebook.CameraActivity

class CameraScene (
    title : String,
    text: String,
    val label : String,
    val okLink: Int,
    val errorLink: Int,
    val notFoundLink : Int
) : Scene(title, text) {
    override fun getActivityType(): Class<*> {
        return CameraActivity::class.java
    }
}