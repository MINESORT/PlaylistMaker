package com.practicum.playlistmakerhomework1

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.AdapterTrack

class SearchActivity : AppCompatActivity() {
    private var text: String = ""
    private lateinit var searchEdit: EditText

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        searchEdit = findViewById(R.id.searchWatcher)

        val xButton = findViewById<ImageView>(R.id.xIcon)
        val trackList: ArrayList<Track> = ArrayList()
        val backButton = findViewById<Button>(R.id.backSettings)

        backButton.setOnClickListener {
            val displayIntent = Intent(this, MainActivity::class.java)
            startActivity(displayIntent)
        }



        xButton.setOnClickListener {
            searchEdit.setText("")
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(searchEdit.windowToken, 0)
        }

        trackList.add(Track("Smells Like Teen Spirit", "Nirvana", "5:01", getString(R.string.nirvana)))
        trackList.add(Track("Billie Jean", "Michael Jackson", "4:35", getString(R.string.michael_jackson)))
        trackList.add(Track("Stayin' Alive", "Bee Gees", "4:10", getString(R.string.bee_gees)))
        trackList.add(Track("Whole Lotta Love", "Led Zeppelin", "5:33", getString(R.string.led_zeppelin)))
        trackList.add(Track("Sweet Child O'Mine", "Guns N' Roses", "5:03", getString(R.string.guns_n_roses)))


        val temporaryTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                xButton.visibility = clearButtonVisibility(p0)
            }

            override fun afterTextChanged(p0: Editable?) {
                text = p0.toString()
            }
        }
        searchEdit.addTextChangedListener(temporaryTextWatcher)

        val tracksRecycler = findViewById<RecyclerView>(R.id.recycler_view)

        tracksRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
             tracksRecycler.adapter = AdapterTrack(trackList)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) { View.GONE } else { View.VISIBLE }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("text", text)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val restoredText = savedInstanceState.getString("text")
        searchEdit.setText(restoredText)
    }
    }
