package com.practicum.playlistmakerhomework1.util

import android.content.Context
import com.practicum.playlistmakerhomework1.player.domain.MediaPlayerInter
import com.practicum.playlistmakerhomework1.player.Ui.MediaPlayerInterLL
import com.practicum.playlistmakerhomework1.search.data.TracksRepositoryImplement
import com.practicum.playlistmakerhomework1.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmakerhomework1.search.domain.api.TracksInteractor
import com.practicum.playlistmakerhomework1.search.domain.api.TracksRepository
import com.practicum.playlistmakerhomework1.search.domain.impl.TracksInteractorImpl

object Creator {
    private fun getTracksRepository(context: Context): TracksRepository{
        return TracksRepositoryImplement(RetrofitNetworkClient(context))
    }
    fun provideTracksInteractor(context: Context): TracksInteractor{
        return TracksInteractorImpl(getTracksRepository(context))
    }
    fun mediaPlayerInteractorProvider(): MediaPlayerInter {
        return MediaPlayerInterLL()
    }
}