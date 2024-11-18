package com.practicum.playlistmakerhomework1.search.presentation

import android.annotation.SuppressLint
import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmakerhomework1.search.domain.api.TracksInteractor
import com.practicum.playlistmakerhomework1.search.domain.models.Track
import com.practicum.playlistmakerhomework1.search.ui.models.TracksState
import com.practicum.playlistmakerhomework1.util.Creator


class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val tracksIt = Creator.provideTracksInteractor(getApplication())
    private val handler = Handler(Looper.getMainLooper())
    private val tracksLiveData = MutableLiveData<TracksState>()

    fun observeState(): LiveData<TracksState> = tracksLiveData

    private var latestSearchText: String? = null

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    @SuppressLint("SuspiciousIndentation")
    fun searchDebounce(changedText: String) {




        if (latestSearchText == changedText) return

                this.latestSearchText = changedText
                handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)



        if (changedText.isEmpty()) {
            renderState(TracksState.Empty) // или Placeholder, если хотите именно заглушку
            return
        }
                val searchRunnable = Runnable { searchRequest(changedText) }
                val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
                handler.postAtTime(searchRunnable, SEARCH_REQUEST_TOKEN, postTime)



    }



    fun repeatRequest() {
        if (latestSearchText.isNullOrEmpty()) {
            renderState(TracksState.Empty)  // Если latestSearchText пустой, показываем заглушку
        } else {
            searchRequest(latestSearchText!!)
        }
    }
    fun forcedRequest(queryInput: String) {
        if (queryInput.isEmpty()) {
            renderState(TracksState.Empty)

       // Если текст пустой, показываем заглушку
        }
            searchRequest(queryInput)

    }

    fun setState(state: TracksState){
        tracksLiveData.postValue(state)
    }

    private fun searchRequest(newSearchText: String) {
        Log.d("SearchViewModel", "searchRequest $newSearchText")

        if (newSearchText.isEmpty()) {
            Log.d("SearchViewModel", "Empty search request")
            renderState(TracksState.Empty)
            return // Выход, если текст пустой
        }

        renderState(TracksState.Loading)

        tracksIt.searchTracks(newSearchText, object : TracksInteractor.TrackConsumer {
            override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                Log.d("SearchViewModel", "consume: foundTracks=$foundTracks, errorMessage=$errorMessage")
                if (errorMessage != null) {
                    renderState(TracksState.Error) // Ошибка запроса
                    return
                }


                if (foundTracks.isNullOrEmpty()) {
                    renderState(TracksState.Empty) // Заглушка "ничего не найдено"
                    Log.d("SearchViewModel", "No tracks found, showing empty state.")
                } else {
                    renderState(TracksState.Content(foundTracks)) // Состояние с найденными треками
                    Log.d("SearchViewModel", "Tracks found: ${foundTracks.size}")
                }

                }

        })
    }

    private fun renderState(state: TracksState) {
        Log.d("SearchViewModel", "renderState: $state")
        tracksLiveData.postValue(state)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }
}