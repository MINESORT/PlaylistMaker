package com.practicum.playlistmakerhomework1.search.data

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import com.practicum.playlistmakerhomework1.search.domain.models.Track

class SearchHistory(private val sharedPreferences: SharedPreferences){

  var historyListTracks = mutableListOf<Track>()


    fun getTracks(){
        val Sample  = sharedPreferences.getString(HISTORY_KEY, null)
        historyListTracks = listFromJson(Sample)
    }

    fun putTracks(){
        val historyValue = jsonFromList(historyListTracks)
        sharedPreferences.edit().putString(HISTORY_KEY, historyValue).apply()
    }

    private fun jsonFromList(list: MutableList<Track>): String{
        val gson = Gson()
        return gson.toJson(list)
    }

    private fun listFromJson(json: String?): MutableList<Track> {
        val gson = Gson()
        val listT = object : TypeToken<MutableList<Track>>() {}.type
        return gson.fromJson(json, listT) ?: mutableListOf()
    }

    fun addTrack(track: Track){
        when {
            historyListTracks.contains(track) -> {
                historyListTracks.remove(track)
            }
        }
        when {
            historyListTracks.size >= MAX_SIZE -> {
                historyListTracks.removeAt(LAST_INDEX)
            }
        }
        historyListTracks.add(FIRST_INDEX, track)
    }

    companion object{
        private const val HISTORY_KEY = "history"
        private const val MAX_SIZE = 10
        private const val LAST_INDEX = 9
        private const val FIRST_INDEX = 0
    }
}