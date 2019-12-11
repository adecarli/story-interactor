package com.example.gamebook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu

class NotebookActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notebook)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.notebook_app_bar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
