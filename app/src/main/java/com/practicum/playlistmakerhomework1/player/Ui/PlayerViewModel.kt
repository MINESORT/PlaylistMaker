package com.practicum.playlistmakerhomework1.player.Ui


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmakerhomework1.player.Ui.models.PlayerState

class PlayerViewModel : ViewModel() {

    private val stateLiveData = MutableLiveData<PlayerState>()
    fun observeState(): LiveData<PlayerState> = stateLiveData

    fun play(){
        stateLiveData.postValue(PlayerState.Playing)
    }

    fun pause(){
        stateLiveData.postValue(PlayerState.Paused)
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel()
            }
        }
    }
}