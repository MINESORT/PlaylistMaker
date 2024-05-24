package com.practicum.playlistmakerhomework1

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private const val LAST_INDEX = 9
private const val FIRST_INDEX = 0
private const val HISTORY_KEY = "history"
private const val MAX_SIZE = 10


class SearchHistory(private val sharedPreferences: SharedPreferences){

  val historyListTracks = mutableListOf<Track>()

    fun getTracks(): List<Track> {
        val s = sharedPreferences.getString(HISTORY_KEY, null)
        return listFromJson(s)
    }

    fun putTracks(){
        val s = jsonFromList(historyListTracks)
        sharedPreferences.edit().putString(HISTORY_KEY, s).apply()
    }

    private fun jsonFromList(list: MutableList<Track>): String{
        val gson = Gson()
        return gson.toJson(list)
    }

    private fun listFromJson(json: String?): MutableList<Track>{
        val gson = Gson()
        val listType = object : TypeToken<MutableList<Track>>() {}.type
        return gson.fromJson(json, listType) ?: mutableListOf()
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

}