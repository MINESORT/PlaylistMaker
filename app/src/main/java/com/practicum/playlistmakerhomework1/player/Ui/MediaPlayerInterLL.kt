package com.practicum.playlistmakerhomework1.player.Ui

import android.media.MediaPlayer
import com.practicum.playlistmakerhomework1.player.domain.MediaPlayerInter

class MediaPlayerInterLL: MediaPlayerInter {

    override var url: String? = null

    private val player = MediaPlayer()
    override var playerState = STATE_DEFAULT

    override fun prepare(callback: () -> Unit) {
        player.setDataSource(url)
        player.prepareAsync()
        player.setOnPreparedListener {
            playerState = STATE_PREPARED
            callback()
        }
        player.setOnCompletionListener {
            playerState = STATE_PREPARED
            callback()
        }
    }

    override fun start(callback: () -> Unit) {
        callback()
        player.start()
        playerState = STATE_PLAYING
    }

    override fun pause(callback: () -> Unit) {
        callback()
        player.pause()
        playerState = STATE_PAUSED
    }

    override fun release() {
        player.release()
    }

    override fun playbackControl(isPlaying: () -> Unit, isPause: () -> Unit) {
        when (playerState) {
            STATE_PLAYING -> {
                pause { isPlaying() }
            }

            STATE_PREPARED, STATE_PAUSED -> {
                start { isPause() }
            }
        }
    }

    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
    }

}