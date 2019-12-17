package com.example.gamebook

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.gamebook.data.database.ApplicationDatabase
import kotlinx.android.synthetic.main.activity_game_over.*
import org.jetbrains.anko.doAsync

class GameOverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)

        val id = intent.getLongExtra("game_id", -1)

        if (id == -1L)
            finish()

        val db = ApplicationDatabase.getInstance(this)
        doAsync {
            val dao = db.serializedGameDao()
            val serializedGame = dao.get(id)!!
            val game = Parser.fromJson(serializedGame.json)!!

            runOnUiThread {
                scene_title.text = game.getCurrentScene().title
                scene_text.text = game.getCurrentScene().text
            }

            button_next.setOnClickListener {
                doAsync {
                    dao.delete(serializedGame)

                    runOnUiThread {
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
