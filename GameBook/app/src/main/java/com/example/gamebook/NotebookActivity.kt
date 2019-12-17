package com.example.gamebook

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gamebook.data.database.ApplicationDatabase
import kotlinx.android.synthetic.main.activity_notebook.*
import org.jetbrains.anko.doAsync

class NotebookActivity : AppCompatActivity() {

    private var gameId : Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notebook)

        gameId = intent.getLongExtra("game_id", -1)

        if (gameId == -1L)
            finish()

        val db = ApplicationDatabase.getInstance(this)
        doAsync {
            val game = db.serializedGameDao().get(gameId)!!
            runOnUiThread {
                text_view.setText(game.notes)
            }
        }
    }

    override fun onDestroy() {
        val db = ApplicationDatabase.getInstance(this)
        doAsync {
            val game = db.serializedGameDao().get(gameId)!!
            game.notes = text_view.text.toString()
            db.serializedGameDao().update(game)
        }
        super.onDestroy()
    }
}
