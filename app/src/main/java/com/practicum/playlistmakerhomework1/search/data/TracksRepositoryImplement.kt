package com.practicum.playlistmakerhomework1.search.data

import com.practicum.playlistmakerhomework1.search.data.dto.ResponseTrack
import com.practicum.playlistmakerhomework1.search.data.dto.TracksSearchRequest
import com.practicum.playlistmakerhomework1.search.domain.models.Track
import com.practicum.playlistmakerhomework1.search.domain.api.TracksRepository
import com.practicum.playlistmakerhomework1.util.Resource



class TracksRepositoryImplement(private val networkClient: Network) : TracksRepository {
    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        return when (response.resultCode) {
            -1 -> {
                Resource.Error("Проверьте подключение к интернету")
            }

            200 -> {
                Resource.Succes((response as ResponseTrack).results.map {
                    Track(
                        it.trackName,
                        it.artistName,
                        it.trackTimeMillis,
                        it.artworkUrl100,
                        it.collectionName,
                        it.releaseDate,
                        it.primaryGenreName,
                        it.country,
                        it.previewUrl
                    )
                })
            }

            else -> {
                Resource.Error("Ошибка сервера")
            }
        }
    }
}