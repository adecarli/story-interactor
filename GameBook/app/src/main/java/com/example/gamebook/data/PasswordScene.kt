package com.example.gamebook.data

class PasswordScene (
    title : String,
    text : String,
    val password : String,
    val okLink : Int,
    val errorLink : Int
) : Scene(title, text)