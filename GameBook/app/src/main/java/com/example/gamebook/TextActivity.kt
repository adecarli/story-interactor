package com.example.gamebook

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.gamebook.data.TextScene
import com.example.gamebook.data.database.SerializedGame
import com.example.gamebook.data.database.SerializedGameDatabase
import kotlinx.android.synthetic.main.activity_text.*
import org.jetbrains.anko.doAsync
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class TextActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text)

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
                val time = ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME)
                game.currentSceneIndex = (game.getCurrentScene() as TextScene).okLink
                val newSerializedGame = SerializedGame(serializedGame.uid, Parser.toJson(game), time)

                doAsync {
                    dao.update(newSerializedGame)

                    runOnUiThread {
                        val intent = Intent(this@TextActivity, game.getCurrentActivity())
                        intent.putExtra("game_id", id)
                        startActivity(intent)
                        finish()
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
