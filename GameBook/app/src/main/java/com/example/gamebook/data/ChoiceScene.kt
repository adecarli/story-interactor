package com.example.gamebook.data

class ChoiceScene (
    title : String,
    text: String,
    val choices : Array<Choice>
) : Scene(title, text)
