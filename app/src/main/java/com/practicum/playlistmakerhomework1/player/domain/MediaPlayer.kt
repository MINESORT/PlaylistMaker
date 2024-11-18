package com.practicum.playlistmakerhomework1.player.domain

interface MediaPlayer {
    fun setDataSource(url: String?)
    fun prepareAsync()
    fun start()
    fun pause()
    fun release()
    fun setOnPreparedListener(listener: () -> Unit)
    fun setOnCompletionListener(listener: () -> Unit)
}