package com.example.gamebook

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import com.example.gamebook.data.ChoiceScene
import com.example.gamebook.data.database.SerializedGame
import com.example.gamebook.data.database.SerializedGameDatabase
import kotlinx.android.synthetic.main.activity_choice.*
import kotlinx.android.synthetic.main.activity_choice.button_next
import org.jetbrains.anko.doAsync
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ChoiceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choice)

        val id = intent.getLongExtra("game_id", -1)

        if (id == -1L)
            finish()

        val db = SerializedGameDatabase.getInstance(this)
        doAsync {
            val dao = db.serializedGameDao()
            val serializedGame = dao.get(id)!!
            val game = Parser.fromJson(serializedGame.json)!!

            val currentScene = game.getCurrentScene() as ChoiceScene

            runOnUiThread {
                scene_title.text = game.getCurrentScene().title
                scene_text.text = game.getCurrentScene().text

                for (choice in currentScene.choices) {
                    val radioButton = RadioButton(this@ChoiceActivity)
                    radioButton.text = choice.name
                        radioButton.layoutParams = RadioGroup.LayoutParams(
                        RadioGroup.LayoutParams.MATCH_PARENT,
                        RadioGroup.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                    radioButton.buttonTintList = ColorStateList.valueOf(
                        resources.getColor(R.color.colorPrimary)
                    )
                    radioButton.setTextColor(ColorStateList.valueOf(
                        resources.getColor(R.color.colorPrimaryLight)
                    ))
                    radioButton.setPadding(20)
                    radio_group.addView(radioButton)
                }
            }

            button_next.setOnClickListener {
                val time = ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME)

                game.currentSceneIndex = currentScene.choices.filter { el ->
                    el.name == radio_group.findViewById<RadioButton>(radio_group.checkedRadioButtonId).text
                }.first().link

                val newSerializedGame = SerializedGame(serializedGame.uid, Parser.toJson(game), time)

                doAsync {
                    dao.update(newSerializedGame)

                    runOnUiThread {
                        val intent = Intent(this@ChoiceActivity, game.getCurrentActivity())
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
