package com.practicum.playlistmakerhomework1.search.data.network


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.practicum.playlistmakerhomework1.search.data.Network
import com.practicum.playlistmakerhomework1.search.data.dto.Response
import com.practicum.playlistmakerhomework1.search.data.dto.TracksSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient(private val context: Context): Network {

    private val baseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesAPI::class.java)
    override fun doRequest(dto: Any): Response {
        when {
            !isConnected() -> {
                return Response().apply { resultCode = -1 }
            }
            dto !is TracksSearchRequest -> {
                return Response().apply { resultCode = 400 }
            }
            else -> {
                val response = iTunesService.getTrack(dto.expression).execute()

                val body = response.body()

                return when {
                    body != null -> {
                        body.apply { resultCode = response.code() }
                    }
                    else -> Response().apply { resultCode = response.code() }
                }
            }
        }

    }

    private fun isConnected():Boolean{
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

        when {
            capabilities != null -> {
                when{
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
                }
            }
        }
        return false
    }
}
