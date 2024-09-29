package com.practicum.playlistmakerhomework1

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity


class SearchActivity : AppCompatActivity() {

    private var isClicked = true
    private val handler = Handler(Looper.getMainLooper())
    private val searchRun = Runnable { apiRequest(text) }
    private lateinit var errorNotFound: LinearLayout
    private lateinit var errorNoInternet: LinearLayout
    private lateinit var updateButton: Button
    private lateinit var trackRecycler: RecyclerView
    private lateinit var history: LinearLayout
    private lateinit var tracks: MutableList<Track>
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: AdapterTrack
    private var text: String = ""
    private lateinit var searchEdit: EditText
    private val baseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build()
    private val iTunes = retrofit.create(ITunesAPI::class.java)
    private lateinit var scanHistory: SearchHistory
    private lateinit var historyAd: AdapterTrack
    @SuppressLint("NotifyDataSetChanged")
    override fun onStop() {
        super.onStop()
        scanHistory.putTracks()
        historyAd.notifyDataSetChanged()
    }
    @SuppressLint("MissingInflatedId", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_search)
         searchEdit = findViewById(R.id.searchWatcher)
         updateButton = findViewById(R.id.update_button)
         trackRecycler = findViewById(R.id.recycler_view)
         errorNotFound = findViewById(R.id.error_not_found)
         errorNoInternet = findViewById(R.id.error_no_internet)
         history = findViewById(R.id.history_layout)
         tracks = mutableListOf()
         progressBar = findViewById(R.id.progressBar)
        val xButton = findViewById<ImageView>(R.id.xIcon)
        val backButton = findViewById<Button>(R.id.backSettings)
        val historySharedPrefs = getSharedPreferences(HISTORY_KEY, MODE_PRIVATE)
        scanHistory = SearchHistory(historySharedPrefs)
        scanHistory.getTracks()
        val mediaIntent = Intent(this, PlayerActivity::class.java)

        adapter = AdapterTrack(
            tracks, callback = { track ->  scanHistory.addTrack(track)
                when {
                    clickDebounce() -> {
                        startActivity(mediaIntent.putExtra(INTENT_KEY, track))
                    }
                }
            }
        )

        val historyRecyclerView = findViewById<RecyclerView>(R.id.history_rec)

        history.visibility = when { scanHistory.historyListTracks.isEmpty() -> View.GONE else -> View.VISIBLE }

        historyAd = AdapterTrack(
            scanHistory.historyListTracks, callback = { track ->  scanHistory.addTrack(track)
                when {
                    clickDebounce() -> {
                        startActivity(mediaIntent.putExtra(INTENT_KEY, track))
                    }
                }
            }
        )

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
            finish()
        }

        xButton.setOnClickListener {
            searchEdit.setText("")
            tracks.clear()
            adapter.notifyDataSetChanged()
            when {
                scanHistory.historyListTracks.isEmpty() -> history.visibility = View.GONE
            }
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
                when {
                    searchEdit.hasFocus() && p0?.isEmpty() == false -> history.visibility = View.GONE
                    else -> {
                        history.visibility = View.VISIBLE
                        historyAd.notifyDataSetChanged()
                    }
                }
                when {
                    p0.toString().isNotEmpty() -> {
                        searchDebounce()
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                text = p0.toString()
            }
        }
        searchEdit.addTextChangedListener(temporaryTextWatcher)
        searchEdit.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    apiRequest(text)

                }
            }
            false
        }

        updateButton.setOnClickListener {
            apiRequest(text)
            errorNoInternet.visibility = View.GONE
        }
    }
    private fun apiRequest(text: String) {
        progressBar.visibility = View.VISIBLE
        iTunes.getTrack(text).enqueue(object : Callback<ResponseTrack> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<ResponseTrack>,
                response: Response<ResponseTrack>
            ) {
                val respTracks = response.body()?.results

                when {
                    response.isSuccessful && respTracks != null -> {
                        when {
                            respTracks.isEmpty() -> {
                                progressBar.visibility = View.GONE
                                errorNotFound.visibility = View.VISIBLE
                                errorNoInternet.visibility = View.GONE
                                tracks.clear()
                            }
                            else -> {
                                progressBar.visibility = View.GONE
                                tracks.clear()
                                tracks.addAll(respTracks.toMutableList())
                                errorNotFound.visibility = View.GONE
                                errorNoInternet.visibility = View.GONE
                            }
                        }
                    }
                    else -> {
                        progressBar.visibility = View.GONE
                        tracks.clear()
                        errorNoInternet.visibility = View.VISIBLE
                        errorNotFound.visibility = View.GONE
                    }
                }
                adapter.notifyDataSetChanged()
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onFailure(call: Call<ResponseTrack>, t: Throwable) {
                progressBar.visibility = View.GONE
                errorNotFound.visibility = View.GONE
                errorNoInternet.visibility = View.VISIBLE
                tracks.clear()
                adapter.notifyDataSetChanged()
            }
        })
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
    private fun clickDebounce(): Boolean {
        val current = isClicked
        when {
            isClicked -> {
                isClicked = false
                handler.postDelayed({ isClicked = true }, PUSH_DELAY)
            }
        }
        return current
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRun)
        handler.postDelayed(searchRun, SCAN_DELAY)
    }

    companion object {
        const val INTENT_KEY = "key"
        private const val HISTORY_KEY = "history"
        private const val SCAN_DELAY = 2000L
        private const val PUSH_DELAY = 1000L

    }
    }
