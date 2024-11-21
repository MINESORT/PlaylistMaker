package com.practicum.playlistmakerhomework1.search.data.network


import com.practicum.playlistmakerhomework1.search.data.dto.ResponseTrack
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesAPI {
    @GET("/search?entity=song")
    fun getTrack(@Query("term") text: String) : Call<ResponseTrack>
}