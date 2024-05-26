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
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.widget.LinearLayout
import android.widget.Button
import android.view.inputmethod.EditorInfo


private const val HISTORY_KEY = "history"
class SearchActivity : AppCompatActivity() {
    private var text: String = ""
    private lateinit var searchEdit: EditText
    private val baseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build()
    private val iTunes = retrofit.create(ITunesAPI::class.java)
    private lateinit var scanHistory: SearchHistory

    override fun onStop() {
        super.onStop()
        scanHistory.putTracks()
    }
    @SuppressLint("MissingInflatedId", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        searchEdit = findViewById(R.id.searchWatcher)

        val updateButton = findViewById<Button>(R.id.update_button)
        val trackRecycler = findViewById<RecyclerView>(R.id.recycler_view)
        val errorNotFound = findViewById<LinearLayout>(R.id.error_not_found)
        val errorNoInternet = findViewById<LinearLayout>(R.id.error_no_internet)
        val history = findViewById<LinearLayout>(R.id.history_layout)
        val tracks = mutableListOf<Track>()
        val xButton = findViewById<ImageView>(R.id.xIcon)
        val backButton = findViewById<Button>(R.id.backSettings)
        val historySharedPrefs = getSharedPreferences(HISTORY_KEY, MODE_PRIVATE)
        scanHistory = SearchHistory(historySharedPrefs)
        scanHistory.getTracks()

        val adapter = AdapterTrack(tracks) {
            track -> scanHistory.addTrack(track)
        }

        val historyRecyclerView = findViewById<RecyclerView>(R.id.history_rec)
        history.visibility = if (scanHistory.historyListTracks.isEmpty()) View.GONE else View.VISIBLE

        val historyAd = AdapterTrack(scanHistory.historyListTracks) {
            track -> scanHistory.addTrack(track)
        }

        historyRecyclerView.adapter = historyAd
        historyRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        trackRecycler.adapter = adapter
        trackRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val deleteHistory = findViewById<Button>(R.id.delete_history)
        deleteHistory.setOnClickListener{
            historySharedPrefs.edit().clear().apply()
            scanHistory.historyListTracks.clear()
            history.visibility = View.GONE
            historyAd.notifyDataSetChanged()
        }

        backButton.setOnClickListener {
            val displayIntent = Intent(this, MainActivity::class.java)
            startActivity(displayIntent)
        }

        xButton.setOnClickListener {
            searchEdit.setText("")
            tracks.clear()
            adapter.notifyDataSetChanged()
            errorNotFound.visibility = View.GONE
            errorNoInternet.visibility = View.GONE
            historyAd.notifyDataSetChanged()
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(searchEdit.windowToken, 0)
        }

        val temporaryTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                xButton.visibility = clearButtonVisibility(p0)
                if (searchEdit.hasFocus() && p0?.isEmpty() == false) history.visibility = View.GONE
                else {
                    history.visibility = View.VISIBLE
                    historyAd.notifyDataSetChanged()
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                text = p0.toString()
            }
        }
        searchEdit.addTextChangedListener(temporaryTextWatcher)
        fun apiRequest(text: String) {
            iTunes.getTrack(text).enqueue(object : Callback<ResponseTrack> {
                    override fun onResponse(
                        call: Call<ResponseTrack>,
                        response: Response<ResponseTrack>) {
                        val tracksOutResponse = response.body()?.results
                        when {
                            response.isSuccessful && tracksOutResponse != null -> {
                                when {
                                    tracksOutResponse.isEmpty() -> {
                                        errorNotFound.visibility = View.VISIBLE
                                        errorNoInternet.visibility = View.GONE
                                        tracks.clear()
                                    }
                                    else -> {
                                        tracks.clear()
                                        tracks.addAll(tracksOutResponse.toMutableList())
                                        errorNotFound.visibility = View.GONE
                                        errorNoInternet.visibility = View.GONE
                                    }
                                }
                            }
                            else -> {
                                tracks.clear()
                                errorNoInternet.visibility = View.VISIBLE
                                errorNotFound.visibility = View.GONE
                            }
                        }
                        adapter.notifyDataSetChanged()
                    }

                    override fun onFailure(call: Call<ResponseTrack>, t: Throwable) {
                        errorNoInternet.visibility = View.VISIBLE
                        errorNotFound.visibility = View.GONE
                        tracks.clear()
                        adapter.notifyDataSetChanged()
                    }
                })

        }

        searchEdit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                apiRequest(text)
                true
            }
            false
        }

        updateButton.setOnClickListener {
            apiRequest(text)
            errorNoInternet.visibility = View.GONE
        }
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
