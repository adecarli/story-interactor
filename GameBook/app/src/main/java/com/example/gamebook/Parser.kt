package com.example.gamebook

import com.example.gamebook.data.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory

object Parser {
    fun fromJson(json: String) : Game? {
        val adapter : RuntimeTypeAdapterFactory<Scene> =
            RuntimeTypeAdapterFactory
                .of(Scene::class.java)
                .registerSubtype(TextScene::class.java)
                .registerSubtype(PasswordScene::class.java)
                .registerSubtype(ChoiceScene::class.java)
                .registerSubtype(CameraScene::class.java)
                .registerSubtype(GameOverScene::class.java)

        val gson : Gson = GsonBuilder().setPrettyPrinting().registerTypeAdapterFactory(adapter).create()

        return try {
            gson.fromJson(json, Game::class.java)
        } catch (e : JsonSyntaxException) {
            null
        }
    }

    fun toJson(game : Game) : String {
        val adapter : RuntimeTypeAdapterFactory<Scene> =
            RuntimeTypeAdapterFactory
                .of(Scene::class.java)
                .registerSubtype(TextScene::class.java)
                .registerSubtype(PasswordScene::class.java)
                .registerSubtype(ChoiceScene::class.java)
                .registerSubtype(CameraScene::class.java)
                .registerSubtype(GameOverScene::class.java)

        val gson : Gson = GsonBuilder().setPrettyPrinting().registerTypeAdapterFactory(adapter).create()

        return gson.toJson(game)
    }
}