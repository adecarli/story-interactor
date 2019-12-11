package com.example.gamebook.data

class CameraScene (
    title : String,
    text: String,
    val label : String,
    val okLink: Int,
    val errorLink: Int,
    val notFoundLink : Int
) : Scene(title, text)