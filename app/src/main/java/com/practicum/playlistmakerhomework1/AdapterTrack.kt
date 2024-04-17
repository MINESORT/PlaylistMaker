package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmakerhomework1.R
import com.practicum.playlistmakerhomework1.Track
import com.practicum.playlistmakerhomework1.ViewHolderTrack

class AdapterTrack(private val tracks: List<Track>) : RecyclerView.Adapter<ViewHolderTrack>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderTrack {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_track, parent, false)
        return ViewHolderTrack(view)
    }
    override fun onBindViewHolder(holder: ViewHolderTrack, position: Int) {
        holder.bind(tracks[position])
    }
    override fun getItemCount() = tracks.size
}