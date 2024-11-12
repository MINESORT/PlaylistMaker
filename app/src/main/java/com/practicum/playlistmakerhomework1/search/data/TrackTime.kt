package com.practicum.playlistmakerhomework1.search.data

import android.icu.text.SimpleDateFormat
import com.practicum.playlistmakerhomework1.search.domain.models.Track
import java.util.Locale

object TrackTime {
    fun get(track: Track): String{
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
    }
}
