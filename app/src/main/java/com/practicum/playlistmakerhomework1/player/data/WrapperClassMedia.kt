package com.practicum.playlistmakerhomework1.player.data

import com.practicum.playlistmakerhomework1.player.domain.MediaPlayer


class WrapperClassMedia : MediaPlayer {
    private val mediaPlayer = android.media.MediaPlayer()

    override fun setDataSource(url: String?) {
        mediaPlayer.setDataSource(url)
    }

    override fun prepareAsync() {
        mediaPlayer.prepareAsync()
    }

    override fun start() {
        mediaPlayer.start()
    }

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun setOnPreparedListener(listener: () -> Unit) {
        mediaPlayer.setOnPreparedListener { listener() }
    }

    override fun setOnCompletionListener(listener: () -> Unit) {
        mediaPlayer.setOnCompletionListener { listener() }
    }
}