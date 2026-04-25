package com.meganmovies.app.api

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

object ApiService {
    private const val MOVIE_BASE = "https://movieapi.megan.qzz.io/api"
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()
    
    private val gson = Gson()

    suspend fun fetchBanners(): List<Banner> {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("$MOVIE_BASE/homepage/banners")
                    .get().build()
                val response = client.newCall(request).execute()
                val body = response.body?.string() ?: return@withContext emptyList<Banner>()
                val data: BannerResponse = gson.fromJson(body, BannerResponse::class.java)
                data.banners ?: emptyList()
            } catch (e: Exception) { e.printStackTrace(); emptyList() }
        }
    }
}
