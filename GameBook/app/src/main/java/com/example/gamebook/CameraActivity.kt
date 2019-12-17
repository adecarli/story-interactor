package com.example.gamebook

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.gamebook.tensorflow.Keys.INPUT_SIZE
import com.example.gamebook.data.CameraScene
import com.example.gamebook.data.database.ApplicationDatabase
import com.example.gamebook.data.database.SerializedGame
import com.example.gamebook.tensorflow.ImageClassifier
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_camera.*
import org.jetbrains.anko.doAsync
import java.io.*
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class CameraActivity : AppCompatActivity() {

    companion object {
        private const val IMAGE_REQUEST_CODE : Int = 502
    }

    private var gameId : Long = -1
    private lateinit var serializedGame : SerializedGame
    private lateinit var db : ApplicationDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        gameId = intent.getLongExtra("game_id", -1)

        if (gameId == -1L)
            finish()

        db = ApplicationDatabase.getInstance(this)
        doAsync {
            val dao = db.serializedGameDao()
            serializedGame = dao.get(gameId)!!
            val game = Parser.fromJson(serializedGame.json)!!

            val currentScene = game.getCurrentScene() as CameraScene

            runOnUiThread {
                scene_title.text = game.getCurrentScene().title
                scene_text.text = game.getCurrentScene().text
            }

            button_find.setOnClickListener{
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                    takePictureIntent.resolveActivity(packageManager)?.also {
                        startActivityForResult(takePictureIntent, IMAGE_REQUEST_CODE)
                    }
                }
            }

            button_not_find.setOnClickListener {
                val time = ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME)
                game.currentSceneIndex = currentScene.notFoundLink
                val newSerializedGame = SerializedGame(serializedGame.uid, Parser.toJson(game), time)

                doAsync {
                    dao.update(newSerializedGame)

                    runOnUiThread {
                        val intent = Intent(this@CameraActivity, game.getCurrentActivity())
                        intent.putExtra("game_id", gameId)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK)
            try {
                var bitmap = data?.extras?.get("data") as Bitmap
                Log.d("DEBUG", bitmap.width.toString())
                bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false)
                ImageClassifier(assets).recognizeImage(bitmap).subscribeBy(
                    onSuccess = {
                        Log.d("DEBUG", it.toString())

                        val time = ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME)
                        val game = Parser.fromJson(serializedGame.json)!!
                        val currentScene = game.getCurrentScene() as CameraScene

                        if (it.map{ el -> el.title }.contains(currentScene.label))
                            game.currentSceneIndex = currentScene.okLink
                        else
                            game.currentSceneIndex = currentScene.errorLink



                        val newSerializedGame = SerializedGame(serializedGame.uid, Parser.toJson(game), time)
                        doAsync {
                            val dao = db.serializedGameDao()
                            dao.update(newSerializedGame)

                            val intent = Intent(this@CameraActivity, game.getCurrentActivity())
                            intent.putExtra("game_id", serializedGame.uid)
                            startActivity(intent)
                            finish()
                        }
                    }
                )
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_bar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_open_notebook -> {
            val intent = Intent(this, NotebookActivity::class.java)
            intent.putExtra("game_id", gameId)
            startActivity(intent)
            true
        }

        else -> super.onOptionsItemSelected(item)
    }
}