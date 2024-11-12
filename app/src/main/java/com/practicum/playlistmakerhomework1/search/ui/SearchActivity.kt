package com.practicum.playlistmakerhomework1.search.ui

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
import android.widget.LinearLayout
import android.widget.Button
import android.view.inputmethod.EditorInfo
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

import android.util.Log


import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmakerhomework1.R
import com.practicum.playlistmakerhomework1.player.Ui.PlayerActivity
import com.practicum.playlistmakerhomework1.search.data.SearchHistory
import com.practicum.playlistmakerhomework1.search.domain.models.Track
import com.practicum.playlistmakerhomework1.search.presentation.SearchViewModel
import com.practicum.playlistmakerhomework1.search.ui.models.TracksState


class SearchActivity : AppCompatActivity() {

    private var isClicked = true
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var errorNotFound: LinearLayout
    private lateinit var errorNoInternet: LinearLayout
    private lateinit var updateButton: Button
    private lateinit var trackRecycler: RecyclerView
    private lateinit var history: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var searchEdit: EditText
    private lateinit var viewModel: SearchViewModel
    private lateinit var scanHistory: SearchHistory
    private lateinit var textWatcher: TextWatcher
    private lateinit var tracksAdapter: AdapterTrack
    private lateinit var historyAd: AdapterTrack
    private lateinit var qInput: String

    @SuppressLint("NotifyDataSetChanged")
    override fun onStop() {
        super.onStop()
        scanHistory.putTracks()
        Log.d("SearchActivity", "onStop")
    }
    override fun onPause() {
        super.onPause()
        Log.d("SearchActivity", "onPause")
    }

    override fun onResume() {
        super.onResume()
        updateAdapter(historyAd, scanHistory.historyListTracks)
        Log.d("SearchActivity", "onResume")
    }

    override fun onStart() {
        super.onStart()
        Log.d("SearchActivity", "onStart")
    }

    override fun onDestroy() {
        super.onDestroy()
        textWatcher.let { searchEdit.removeTextChangedListener(it) }
        Log.d("SearchActivity", "onDestroy")
    }
    @SuppressLint("MissingInflatedId", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
        Log.d("SearchActivity", "onCreate")
         setContentView(R.layout.activity_search)
         searchEdit = findViewById(R.id.searchWatcher)
         updateButton = findViewById(R.id.update_button)
         trackRecycler = findViewById(R.id.recycler_view)
         errorNotFound = findViewById(R.id.error_not_found)
         errorNoInternet = findViewById(R.id.error_no_internet)
         history = findViewById(R.id.history_layout)
         progressBar = findViewById(R.id.progressBar)
         qInput = ""

        viewModel = ViewModelProvider(this, SearchViewModel.getViewModelFactory())[SearchViewModel::class.java]

        val xButton = findViewById<ImageView>(R.id.xIcon)
        val backButton = findViewById<Button>(R.id.backSettings)
        val historySharedPrefs = getSharedPreferences(HISTORY_KEY, MODE_PRIVATE)
        scanHistory = SearchHistory(historySharedPrefs)
        scanHistory.getTracks()
        val mediaIntent = Intent(this, PlayerActivity::class.java)

        tracksAdapter = AdapterTrack( callback = { track ->  scanHistory.addTrack(track)
                when {
                    clickDebounce() -> {
                        startActivity(mediaIntent.putExtra(INTENT_KEY, track))
                    }
                }
            }
        )

        val historyRecyclerView = findViewById<RecyclerView>(R.id.history_rec)



        historyAd = AdapterTrack(
           callback = { track ->  scanHistory.addTrack(track)
                when {
                    clickDebounce() -> {
                        startActivity(mediaIntent.putExtra(INTENT_KEY, track))
                    }
                }
            }
        )

        historyRecyclerView.adapter = historyAd
        historyRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        trackRecycler.adapter = tracksAdapter
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
            searchEdit.setText(EMPTY)
            viewModel.setState(TracksState.History(scanHistory.historyListTracks))
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(searchEdit.windowToken, 0)
        }


        searchEdit.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus) {
                viewModel.setState(TracksState.History(scanHistory.historyListTracks))
                Log.d("SearchActivity", "editTextHasFocus")
            }
        }

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                xButton.visibility = clearButtonVisibility(p0)
                viewModel.searchDebounce(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
                qInput = p0.toString()
            }
        }
        textWatcher.let { searchEdit.addTextChangedListener(it) }
        searchEdit.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    viewModel.forcedRequest(qInput)

                }
            }
            false
        }

        viewModel.observeState().observe(this){
            render(it)
        }

        updateButton.setOnClickListener {
            viewModel.repeatRequest()
            errorNoInternet.visibility = View.GONE
        }
    }


    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) { View.GONE } else { View.VISIBLE }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("text", qInput)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val restoredText = savedInstanceState.getString("text")
        searchEdit.setText(restoredText)
    }
    private fun render(state:TracksState){
        when(state){
            is TracksState.Loading -> showLoading()
            is TracksState.Content -> showContent(state.tracks)
            is TracksState.Error -> showError()
            is TracksState.Empty -> showEmpty()
            is TracksState.History -> showHistory(scanHistory.historyListTracks)
        }
    }
    private fun showHistory(tracks:List<Track>){
        Log.d("SearchActivity", "showHistory")
        history.visibility =
            if (scanHistory.historyListTracks.isEmpty()) View.GONE else View.VISIBLE

        trackRecycler.visibility = View.GONE
        progressBar.visibility = View.GONE
        errorNoInternet.visibility = View.GONE
        errorNotFound.visibility = View.GONE

        updateAdapter(historyAd, tracks)
    }
    private fun showLoading(){
        Log.d("SearchActivity", "showLoading")
        trackRecycler.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        errorNoInternet.visibility = View.GONE
        errorNotFound.visibility = View.GONE
        history.visibility = View.GONE
    }
    private fun showError(){
        Log.d("SearchActivity", "showError")
        trackRecycler.visibility = View.GONE
        progressBar.visibility = View.GONE
        errorNoInternet.visibility = View.VISIBLE
        errorNotFound.visibility = View.GONE
        history.visibility = View.GONE
    }
    private fun showEmpty(){
        Log.d("SearchActivity", "showEmpty")
        trackRecycler.visibility = View.GONE
        progressBar.visibility = View.GONE
        errorNoInternet.visibility = View.GONE
        errorNotFound.visibility = View.VISIBLE
        history.visibility = View.GONE
    }
    private fun showContent(tracks: List<Track>){
        Log.d("SearchActivity", "showContent")
        trackRecycler.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        errorNoInternet.visibility = View.GONE
        errorNotFound.visibility = View.GONE
        history.visibility = View.GONE

        updateAdapter(tracksAdapter, tracks)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateAdapter(adapter: AdapterTrack, tracks:List<Track>){
        adapter.tracks.clear()
        adapter.tracks.addAll(tracks)
        adapter.notifyDataSetChanged()
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


    companion object {
        private const val EMPTY = ""
        const val INTENT_KEY = "key"
        private const val HISTORY_KEY = "history"
        private const val PUSH_DELAY = 1000L

    }
    }
