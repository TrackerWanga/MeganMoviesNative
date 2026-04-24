package com.megan.movies.api

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

object ApiService {
    private const val MOVIE_BASE = "https://movieapi.megan.qzz.io/api"
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Pragma", "no-cache")
                .build()
            chain.proceed(request)
        }
        .build()
    
    private val gson = Gson()

    // Fetch trending movies
    suspend fun fetchTrending(): List<Movie> {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("$MOVIE_BASE/homepage/trending")
                    .get()
                    .build()
                
                val response = client.newCall(request).execute()
                val body = response.body?.string() ?: return@withContext emptyList<Movie>()
                
                val data: TrendingResponse = gson.fromJson(body, TrendingResponse::class.java)
                data.trending?.map { parseMovie(it) } ?: emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    // Search movies
    suspend fun searchMovies(query: String): List<Movie> {
        return withContext(Dispatchers.IO) {
            try {
                val encodedQuery = URLEncoder.encode(query, "UTF-8")
                val request = Request.Builder()
                    .url("$MOVIE_BASE/search/movies?q=$encodedQuery&limit=30")
                    .get()
                    .build()
                
                val response = client.newCall(request).execute()
                val body = response.body?.string() ?: return@withContext emptyList<Movie>()
                
                val data: SearchResponse = gson.fromJson(body, SearchResponse::class.java)
                data.results?.map { parseMovie(it) } ?: emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    // Fetch movie details
    suspend fun fetchMovieInfo(id: String): Movie? {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("$MOVIE_BASE/movie/$id")
                    .get()
                    .build()
                
                val response = client.newCall(request).execute()
                val body = response.body?.string() ?: return@withContext null
                
                val data: MovieDetailResponse = gson.fromJson(body, MovieDetailResponse::class.java)
                data.data?.let { parseMovieDetail(it) }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    // Fetch homepage
    suspend fun fetchHomepage(): HomepageData? {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("$MOVIE_BASE/homepage")
                    .get()
                    .build()
                
                val response = client.newCall(request).execute()
                val body = response.body?.string() ?: return@withContext null
                
                gson.fromJson(body, HomepageData::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    // Fetch banners - FIXED: banners are directly at root level
    suspend fun fetchBanners(): List<Banner> {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("$MOVIE_BASE/homepage/banners")
                    .get()
                    .build()
                
                val response = client.newCall(request).execute()
                val body = response.body?.string() ?: return@withContext emptyList<Banner>()
                
                // The API returns { "success": true, "total": 11, "banners": [...] }
                // banners is directly at root, NOT nested in data
                val data: BannerResponse = gson.fromJson(body, BannerResponse::class.java)
                println("API Response: total=${data.total}, banners count=${data.banners?.size ?: 0}")
                data.banners ?: emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    // Universal parser
    private fun parseMovie(item: MovieItem): Movie {
        return Movie(
            id = item.subject_id ?: item.id ?: "",
            slug = item.detail_path ?: item.slug ?: "",
            title = item.title ?: item.name ?: "Unknown",
            year = extractYear(item.year, item.releaseDate),
            type = if (item.type == "tv" || item.subjectType == 2) "tv" else "movie",
            rating = item.rating ?: item.imdbRatingValue,
            genres = item.genres ?: item.genre?.split(",")?.map { it.trim() } ?: emptyList(),
            poster = extractImageUrl(item.poster, item.poster_url, item.cover, item.image),
            backdrop = item.backdrop,
            description = item.description ?: item.plot ?: ""
        )
    }

    private fun parseMovieDetail(item: MovieDetailData): Movie {
        return Movie(
            id = item.id ?: "",
            slug = item.detail_path ?: "",
            title = item.title ?: "Unknown",
            year = item.year,
            type = "movie",
            rating = item.rating,
            genres = item.genres ?: emptyList(),
            poster = item.poster?.url ?: "",
            backdrop = item.backdrop?.url,
            description = item.description ?: "",
            streams = item.streams?.map { StreamOption(it.quality ?: "", it.url ?: "") } ?: emptyList(),
            downloads = item.downloads?.map { DownloadOption(it.quality ?: "", it.size_mb ?: 0, it.url ?: "") } ?: emptyList(),
            cast = item.cast?.map { CastMember(it.name ?: "", it.character ?: "", it.avatar ?: "") } ?: emptyList(),
            trailer = item.trailer?.let { Trailer(it.url ?: "", it.duration ?: 0) }
        )
    }

    private fun extractYear(year: Any?, releaseDate: String?): Int? {
        if (year is Number) return year.toInt()
        if (year is String) return year.toIntOrNull()
        if (releaseDate != null) {
            return releaseDate.split("-").firstOrNull()?.toIntOrNull()
        }
        return null
    }

    private fun extractImageUrl(poster: Any?, posterUrl: String?, cover: Any?, image: Any?): String {
        if (poster is String) return poster
        if (posterUrl != null) return posterUrl
        if (cover is Map<*, *>) return cover["url"] as? String ?: ""
        if (image is Map<*, *>) return image["url"] as? String ?: ""
        return ""
    }
}
