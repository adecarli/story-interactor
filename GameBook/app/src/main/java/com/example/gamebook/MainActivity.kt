package com.example.gamebook

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.gamebook.data.Game
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream

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

                    val game : Game? = Parser.parse(outputString)
                    game?.let {
                        Log.d("DEBUG", "yey!")
                    }
                }
            }
        }
    }
}
