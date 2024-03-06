package com.practicum.playlistmakerhomework1

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val settingsButton = findViewById<Button>(R.id.buttonMain3)
        val libraryButton = findViewById<Button>(R.id.buttonMain2)
        val searchButton = findViewById<Button>(R.id.buttonMain1)

        settingsButton.setOnClickListener {
            val displayIntent = Intent(this, SettingsActivity::class.java)
            startActivity(displayIntent)
        }

        val imageClickListener: View.OnClickListener = View.OnClickListener { val displayIntent = Intent(this, SearchActivity::class.java)
            startActivity(displayIntent) }
        searchButton.setOnClickListener(imageClickListener)


        libraryButton.setOnClickListener {
            val displayIntent = Intent(this, MediaLibrary::class.java)
            startActivity(displayIntent)
        }



    }
}