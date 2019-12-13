package com.example.gamebook

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gamebook.data.database.SerializedGame
import com.example.gamebook.data.database.SerializedGameDatabase
import org.jetbrains.anko.doAsync

class StoryChooserActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val serializedGames : MutableList<SerializedGame> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_chooser)

        viewManager = LinearLayoutManager(this)
        viewAdapter = StoryChooserItemAdapter(this.serializedGames, this)

        recyclerView = findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }

        val db = SerializedGameDatabase.getInstance(this)

        doAsync {
            val dao = db.serializedGameDao()
            serializedGames.addAll(dao.getAll())
            runOnUiThread {
                viewAdapter.notifyDataSetChanged()
            }
        }
    }
}