package com.practicum.playlistmakerhomework1


import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmakerhomework1.databinding.ActivityPlayerBinding


class PlayerActivity : AppCompatActivity() {

    private lateinit var bind: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityPlayerBinding.inflate(layoutInflater)
        val viewPlayer = bind.root
        setContentView(viewPlayer)
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        val tracks = intent.getSerializableExtra(SearchActivity.INTENT_KEY) as Track
        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        Glide.with(this).load(tracks.artwork512).centerCrop().placeholder(R.drawable.player_placeholder).transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.corner_2))).into(bind.artwork)

        bind.playTime.text = "0:30"
        bind.lengthR.text = tracks.timesUp
        bind.yearR.text = tracks.releaseDate.substring(0, 4)
        bind.genreR.text = tracks.primaryGenreName
        bind.countryR.text = tracks.country
        bind.trackName.text = tracks.trackName
        bind.artistName.text = tracks.artistName
        bind.collectionNameR.text = tracks.collectionName
    }

}