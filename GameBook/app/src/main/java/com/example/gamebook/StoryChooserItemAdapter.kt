package com.example.gamebook

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gamebook.data.database.SerializedGame
import kotlinx.android.synthetic.main.story_chooser_item.view.*

class StoryChooserItemAdapter(private val serializedGames: List<SerializedGame>, context : Context) :
    RecyclerView.Adapter<StoryChooserItemAdapter.ViewHolder>() {

    class ViewHolder(val view: View, var serializedGame : SerializedGame?) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.story_chooser_item, parent, false)
        return ViewHolder(view, null)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val serializedGame = serializedGames[position]
        val game = Parser.parse(serializedGame.json)!!

        holder.serializedGame = serializedGame
        holder.view.title.text = game.title
        holder.view.last_played.text = serializedGame.lastPlayed
        holder.view.setOnClickListener {
            Log.d("DEBUG", game.title)
        }
    }

    override fun getItemCount() = serializedGames.size
}