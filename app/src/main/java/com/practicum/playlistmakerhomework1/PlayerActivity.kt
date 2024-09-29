package com.practicum.playlistmakerhomework1


import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmakerhomework1.databinding.ActivityPlayerBinding
import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    companion object {
        private const val TIMER_DELAY = 900L
        private const val NONE_TIMER = "00:00"
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private var handler: Handler? = null
    private var startTimer = 0L
    private var url: String? = null
    private var mediaPlayer = MediaPlayer()
    private lateinit var playButton: ImageView
    private lateinit var playTimer: TextView
    private var playerState = STATE_DEFAULT
    private lateinit var bind: ActivityPlayerBinding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityPlayerBinding.inflate(layoutInflater)
        val viewPlayer = bind.root
        setContentView(viewPlayer)
        val tracks = intent.getSerializableExtra(SearchActivity.INTENT_KEY) as Track
        bind.backButton.setOnClickListener {
        finish()
    }

    Glide.with(this).load(tracks.artwork512).centerCrop()
    .placeholder(R.drawable.player_placeholder)
    .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.corner_2)))
    .into(bind.artwork)
    url = tracks.previewUrl

    bind.lengthR.text = tracks.timesUp
    bind.yearR.text = tracks.releaseDate.substring(0, 4)
    bind.genreR.text = tracks.primaryGenreName
    bind.countryR.text = tracks.country
    bind.trackName.text = tracks.trackName
    bind.artistName.text = tracks.artistName
    bind.collectionNameR.text = tracks.collectionName
    playTimer = findViewById(R.id.playTime)
    handler = Handler(Looper.getMainLooper())
    playTimer.text = NONE_TIMER
    playButton = bind.playButton

        when {
            url != null -> {
                playTimer
                preparePlayer()
                playButton.setOnClickListener {
                    playbackWatch()
                }
            }
            else -> {
                playButton.setOnClickListener {
                    Toast.makeText(applicationContext, "Url = null", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
        override fun onPause() {
            super.onPause()
            pausePlayer()
        }

        override fun onDestroy() {
            super.onDestroy()
            mediaPlayer.release()
        }
    @SuppressLint("SetTextI18n")
    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener { playerState = STATE_PREPARED }
        mediaPlayer.setOnCompletionListener {
            playButton.setImageResource(R.drawable.play_button)
            playerState = STATE_PREPARED
            playTimer.text = NONE_TIMER
        }
    }
        private fun startPlayer() {
            mediaPlayer.start()
            playButton.setImageResource(R.drawable.pause_button)
            playerState = STATE_PLAYING
            startTime()
        }

        private fun pausePlayer() {
            mediaPlayer.pause()
            playButton.setImageResource(R.drawable.play_button)
            playerState = STATE_PAUSED
        }
    private fun updateTime(start: Long): Runnable {
        return object : Runnable {
            override fun run() {
                when (playerState) {
                    STATE_PLAYING -> {
                        val current = System.currentTimeMillis()
                        val time = current - start
                        playTimer.text = time.toString()
                        handler?.postDelayed(this, TIMER_DELAY)


                        playTimer.text = SimpleDateFormat(
                            "mm:ss",
                            Locale.getDefault()
                        ).format(mediaPlayer.currentPosition)

                    }

                }
            }
        }

    }
        private fun playbackWatch() {
            when (playerState) {
                STATE_PLAYING -> {
                    pausePlayer()
                }
                STATE_PREPARED, STATE_PAUSED -> {
                    startPlayer()
                }
            }
        }

        private fun startTime() {
            startTimer = System.currentTimeMillis()
            handler?.post(updateTime(startTimer))
        }
    }
