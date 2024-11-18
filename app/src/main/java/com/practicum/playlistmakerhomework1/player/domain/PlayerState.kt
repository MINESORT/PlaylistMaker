package com.practicum.playlistmakerhomework1.player.domain


sealed interface PlayerState{
    data object Playing: PlayerState
    data object Paused: PlayerState
}