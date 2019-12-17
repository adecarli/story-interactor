package com.example.gamebook.data

import java.io.Serializable

abstract class Scene (
    val title : String,
    val text : String
) : Serializable {
    abstract fun getActivityType() : Class<*>
}