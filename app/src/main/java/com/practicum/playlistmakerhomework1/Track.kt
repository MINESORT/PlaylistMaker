package com.practicum.playlistmakerhomework1

import android.icu.text.SimpleDateFormat
import android.os.Parcelable
import java.io.Serializable
import java.util.Locale
data class Track(
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val playTimeD: String ="0:30"


): Serializable {
    val artwork512: String
        get() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
    val timesUp: String get() =
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)
}
