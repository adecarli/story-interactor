package com.example.gamebook

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gamebook.data.database.ApplicationDatabase
import com.example.gamebook.data.database.SerializedGame
import kotlinx.android.synthetic.main.story_chooser_item.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class StoryChooserItemAdapter(private val serializedGames: List<SerializedGame>, val context : Context) :
    RecyclerView.Adapter<StoryChooserItemAdapter.ViewHolder>() {

    class ViewHolder(val view: View, var serializedGame : SerializedGame?) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.story_chooser_item, parent, false)
        return ViewHolder(view, null)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val serializedGame = serializedGames[position]
        val game = Parser.fromJson(serializedGame.json)!!

        val db = ApplicationDatabase.getInstance(context)

        holder.serializedGame = serializedGame
        holder.view.title.text = game.title
        holder.view.last_played.text = serializedGame.lastPlayed
        holder.view.setOnClickListener {
            doAsync {
                val dao = db.serializedGameDao()
                val time = ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME)
                serializedGame.lastPlayed = time

                dao.update(serializedGame)

                context.runOnUiThread {
                    val intent = Intent(this, game.getCurrentActivity())
                    intent.putExtra("game_id", serializedGame.uid)
                    startActivity(intent)
                    (context as Activity).finish()
                }
            }
        }
        holder.view.button_delete.setOnClickListener {
            doAsync {
                val dao = db.serializedGameDao()
                dao.delete(serializedGame)

                (context as Activity).finish()
                context.startActivity(context.intent)
            }
        }
    }

    override fun getItemCount() = serializedGames.size
}