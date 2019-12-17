package com.example.gamebook

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.gamebook.data.Game
import com.example.gamebook.data.database.ApplicationDatabase
import com.example.gamebook.data.database.SerializedGame
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    companion object {
        private const val NEW_STORY_REQUEST_CODE = 501
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        new_adventure_button.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/*"
            startActivityForResult(intent, NEW_STORY_REQUEST_CODE)
        }

        continue_adventure_button.setOnClickListener {
            val intent = Intent(this, StoryChooserActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == NEW_STORY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val uri = data?.data
                if (uri != null) {
                    val inputStream = contentResolver.openInputStream(uri)

                    val outputStream = ByteArrayOutputStream()
                    inputStream.use { input ->
                        outputStream.use { output ->
                            input?.copyTo(output)
                        }
                    }
                    val byteArray = outputStream.toByteArray()
                    val outputString = String(byteArray, Charsets.UTF_8)

                    val game : Game? = Parser.fromJson(outputString)

                    if (game != null) {
                        val db = ApplicationDatabase.getInstance(this)

                        doAsync {
                            try {
                                val dao = db.serializedGameDao()
                                val time = ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME)
                                val id = dao.insert(SerializedGame(0, outputString, time))

                                runOnUiThread {
                                    val intent = Intent(this.weakRef.get(), game.getCurrentActivity())
                                    intent.putExtra("game_id", id)
                                    startActivity(intent)
                                }
                            } catch (e: Exception) {
                                    Log.d("DEBUG", e.toString())
                            }
                        }
                    }
                }
            }
        }
    }
}
