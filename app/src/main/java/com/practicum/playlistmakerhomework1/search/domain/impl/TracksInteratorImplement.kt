package com.practicum.playlistmakerhomework1.search.domain.impl

import com.practicum.playlistmakerhomework1.search.domain.api.TracksInteractor
import com.practicum.playlistmakerhomework1.search.domain.api.TracksRepository
import com.practicum.playlistmakerhomework1.util.Resource
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository): TracksInteractor {
    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: TracksInteractor.TrackConsumer) {
        executor.execute{
            when (val resource = repository.searchTracks(expression)){
                is Resource.Succes -> {
                    consumer.consume(resource.data, null)
                }
                is Resource.Error -> {
                    consumer.consume(null, resource.message)
                }
            }
        }
    }
}