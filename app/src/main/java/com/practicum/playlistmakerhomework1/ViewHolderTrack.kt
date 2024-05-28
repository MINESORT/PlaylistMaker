
package com.practicum.playlistmakerhomework1


import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import android.icu.text.SimpleDateFormat
import java.util.Locale

class ViewHolderTrack(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val Musician = itemView.findViewById<TextView>(R.id.musician)
    private val Time = itemView.findViewById<TextView>(R.id.time_place_holder)
    private val imagePlaceHolder = itemView.findViewById<ImageView>(R.id.image_place_holder)
    private val TrackName = itemView.findViewById<TextView>(R.id.track_name)
    fun bind(model: Track){
        Glide.with(itemView).load(model.artworkUrl100).centerCrop().placeholder(R.drawable.placeholder).transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.corner_2))).into(imagePlaceHolder)
        TrackName.text = model.trackName
        Musician.text = model.artistName
        Time.text =  model.timesUp
    }
}
