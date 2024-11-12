package com.practicum.playlistmakerhomework1.search.data

import com.practicum.playlistmakerhomework1.search.data.dto.Response

interface Network {
    fun doRequest(dto: Any): Response
}