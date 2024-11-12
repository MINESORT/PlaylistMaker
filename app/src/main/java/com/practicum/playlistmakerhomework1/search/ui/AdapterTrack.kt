package com.practicum.playlistmakerhomework1.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmakerhomework1.R

import com.practicum.playlistmakerhomework1.search.domain.models.Track

class AdapterTrack( private val callback: (Track) -> Unit) :
    RecyclerView.Adapter<ViewHolderTrack>() {
    var tracks = ArrayList<Track>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderTrack {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_track, parent, false)
        return ViewHolderTrack(view)
    }

    override fun onBindViewHolder(holder: ViewHolderTrack, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            callback(tracks[position])

        }
    }
    override fun getItemCount() = tracks.size
}
