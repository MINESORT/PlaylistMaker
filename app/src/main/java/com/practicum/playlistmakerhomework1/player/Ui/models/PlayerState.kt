package com.practicum.playlistmakerhomework1.player.Ui.models


sealed interface PlayerState{
    data object Playing: PlayerState
    data object Paused: PlayerState
}