package com.example.gamebook.data

import java.io.Serializable

data class Choice(
    val name : String,
    val link: Int
) : Serializable