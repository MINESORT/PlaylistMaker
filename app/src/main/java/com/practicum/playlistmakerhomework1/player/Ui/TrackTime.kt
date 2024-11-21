package com.practicum.playlistmakerhomework1.player.Ui

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.os.Handler
import android.os.Looper
import java.util.Date

 class TrackTime( val callback: (String) -> Boolean){

    private var handler = Handler(Looper.getMainLooper())
    fun start() {
        val startTime = System.currentTimeMillis()
        handler.post(update(startTime))
    }

     private fun update(start: Long): Runnable {
         return object : Runnable {
             override fun run() {
                 val current = System.currentTimeMillis()
                 val time = current - start
                 val text = formatMilliseconds(time)
                 val result = callback(text)
                 when {
                     result -> handler.postDelayed(this, 1000L)
                 }




             }
         }
     }




    @SuppressLint("SimpleDateFormat")
    fun formatMilliseconds(milliseconds: Long): String {
        val format = SimpleDateFormat("mm:ss")
        return format.format(Date(milliseconds))
    }





}


