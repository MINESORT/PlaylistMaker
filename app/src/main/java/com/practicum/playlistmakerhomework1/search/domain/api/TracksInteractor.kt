package com.practicum.playlistmakerhomework1.search.domain.api

import com.practicum.playlistmakerhomework1.search.domain.models.Track

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: TrackConsumer)
    interface TrackConsumer {
        fun consume(foundTracks: List<Track>?, errorMessage: String?)
    }
}