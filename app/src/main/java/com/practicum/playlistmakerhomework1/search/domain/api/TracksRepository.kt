package com.practicum.playlistmakerhomework1.search.domain.api

import com.practicum.playlistmakerhomework1.search.domain.models.Track
import com.practicum.playlistmakerhomework1.util.Resource

interface TracksRepository {
    fun searchTracks(expression: String): Resource<List<Track>>
}