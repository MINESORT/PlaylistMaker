package com.practicum.playlistmakerhomework1.player.Ui


import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmakerhomework1.databinding.ActivityPlayerBinding
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmakerhomework1.R
import androidx.lifecycle.ViewModelProvider
import android.util.Log
import android.widget.TextView
import com.practicum.playlistmakerhomework1.player.data.TrackTime
import com.practicum.playlistmakerhomework1.player.domain.MediaPlayerWrapper
import com.practicum.playlistmakerhomework1.player.domain.PlayerState
import com.practicum.playlistmakerhomework1.player.domain.MediaPlayerInterLL
import com.practicum.playlistmakerhomework1.search.domain.models.Track
import com.practicum.playlistmakerhomework1.search.ui.SearchActivity.Companion.INTENT_KEY
import com.practicum.playlistmakerhomework1.util.Creator



class PlayerActivity : AppCompatActivity() {


    private var handler: Handler? = null


    private lateinit var mediaPlayer: MediaPlayerWrapper
    private lateinit var playButton: ImageView
    private lateinit var playTime: TextView
    private lateinit var viewModel: PlayerViewModel
    private lateinit var bind: ActivityPlayerBinding
    @SuppressLint("SetTextI18n", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityPlayerBinding.inflate(layoutInflater)
        val viewPlayer = bind.root
        setContentView(viewPlayer)

        val tracks = intent.getSerializableExtra(INTENT_KEY) as Track

        mediaPlayer = Creator.mediaPlayerInteractorProvider()

        viewModel = ViewModelProvider(this, PlayerViewModel.getViewModelFactory())[PlayerViewModel::class.java]

        bind.backButton.setOnClickListener {
            Log.d("PlayerActivity", "${mediaPlayer.playerState}")
        finish()
    }

    Glide.with(this).load(tracks.artwork512).centerCrop()
    .placeholder(R.drawable.player_placeholder)
    .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.corner_2)))
    .into(bind.artwork)
    mediaPlayer.url = tracks.previewUrl
    bind.lengthR.text = TrackTime.get(tracks)
    bind.yearR.text = tracks.releaseDate.substring(0, 4)
    bind.genreR.text = tracks.primaryGenreName
    bind.countryR.text = tracks.country
    bind.trackName.text = tracks.trackName
    bind.artistName.text = tracks.artistName
    bind.collectionNameR.text = tracks.collectionName
    playTime = findViewById(R.id.playTime)
    handler = Handler(Looper.getMainLooper())
    playTime.text = "00:00"
    playButton = bind.playButton

        val timer = TrackTime { text ->
            playTime.text = text
            mediaPlayer.playerState == MediaPlayerInterLL.STATE_PLAYING
        }
        viewModel.observeState().observe(this){
            rendering(it)
        }

        when {
            mediaPlayer.url != null -> {
                playTime
                mediaPlayer.prepare { playButton.setImageResource(R.drawable.play_button) }
                playButton.setOnClickListener {
                    mediaPlayer.playbackControl(
                        {

                            viewModel.pause()
                        },
                        {

                            viewModel.play()
                            timer.start()
                        }
                    )
                }

            }
            else -> {
                playButton.setOnClickListener {
                    showToast()
                }
            }
        }
    }

    private fun rendering(state: PlayerState){
        when(state){
            is PlayerState.Playing -> playButton.setImageResource(R.drawable.pause_button)
            is PlayerState.Paused -> playButton.setImageResource(R.drawable.play_button)
        }
    }

    private fun showToast(){
        Toast.makeText(applicationContext, "Url = null", Toast.LENGTH_SHORT).show()
    }

        override fun onPause() {

            super.onPause()

            mediaPlayer.pause {
                playButton.setImageResource(R.drawable.play_button)
            }
        }
        override fun onDestroy() {
            super.onDestroy()
            mediaPlayer.release()
        }



}







