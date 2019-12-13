package com.example.gamebook

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.gamebook.data.PasswordScene
import com.example.gamebook.data.TextScene
import com.example.gamebook.data.database.SerializedGame
import com.example.gamebook.data.database.SerializedGameDatabase
import kotlinx.android.synthetic.main.activity_password.*
import kotlinx.android.synthetic.main.activity_password.button_next
import kotlinx.android.synthetic.main.activity_password.scene_text
import kotlinx.android.synthetic.main.activity_password.scene_title
import kotlinx.android.synthetic.main.activity_text.*
import org.jetbrains.anko.doAsync
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class PasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)

        val id = intent.getLongExtra("game_id", -1)

        if (id == -1L)
            finish()

        val db = SerializedGameDatabase.getInstance(this)
        doAsync {
            val dao = db.serializedGameDao()
            val serializedGame = dao.get(id)!!
            val game = Parser.fromJson(serializedGame.json)!!

            runOnUiThread {
                scene_title.text = game.getCurrentScene().title
                scene_text.text = game.getCurrentScene().text
            }

            button_next.setOnClickListener {
                val currentScene = game.getCurrentScene() as PasswordScene

                val time = ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME)

                val actual = password.text.toString()
                val expected = currentScene.password
                game.currentSceneIndex = if (actual == expected) currentScene.okLink else currentScene.errorLink

                val newSerializedGame = SerializedGame(serializedGame.uid, Parser.toJson(game), time)

                doAsync {
                    dao.update(newSerializedGame)

                    runOnUiThread {
                        val intent = Intent(this@PasswordActivity, game.getCurrentActivity())
                        intent.putExtra("game_id", id)
                        startActivity(intent)
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_bar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_open_notebook -> {
            val intent = Intent(this, NotebookActivity::class.java)
            startActivity(intent)
            true
        }

        else -> super.onOptionsItemSelected(item)
    }
}
