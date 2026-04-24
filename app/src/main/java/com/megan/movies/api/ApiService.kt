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

    suspend fun fetchTrending(): List<Movie> {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("$MOVIE_BASE/homepage/trending")
                    .get().build()
                val response = client.newCall(request).execute()
                val body = response.body?.string() ?: return@withContext emptyList<Movie>()
                val data: TrendingResponse = gson.fromJson(body, TrendingResponse::class.java)
                data.trending?.map { parseMovie(it) } ?: emptyList()
            } catch (e: Exception) { e.printStackTrace(); emptyList() }
        }
    }

    suspend fun searchMovies(query: String): List<Movie> {
        return withContext(Dispatchers.IO) {
            try {
                val encodedQuery = URLEncoder.encode(query, "UTF-8")
                val request = Request.Builder()
                    .url("$MOVIE_BASE/search/movies?q=$encodedQuery&limit=30")
                    .get().build()
                val response = client.newCall(request).execute()
                val body = response.body?.string() ?: return@withContext emptyList<Movie>()
                val data: SearchResponse = gson.fromJson(body, SearchResponse::class.java)
                data.results?.map { parseMovie(it) } ?: emptyList()
            } catch (e: Exception) { e.printStackTrace(); emptyList() }
        }
    }

    suspend fun fetchMovieInfo(id: String): Movie? {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("$MOVIE_BASE/movie/$id")
                    .get().build()
                val response = client.newCall(request).execute()
                val body = response.body?.string() ?: return@withContext null
                val data: MovieDetailResponse = gson.fromJson(body, MovieDetailResponse::class.java)
                data.data?.let { parseMovieDetail(it) }
            } catch (e: Exception) { e.printStackTrace(); null }
        }
    }

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

    suspend fun fetchActionMovies(): List<Movie> {
        return fetchSection("action")
    }

    suspend fun fetchHorrorMovies(): List<Movie> {
        return fetchSection("horror")
    }

    suspend fun fetchRomanceMovies(): List<Movie> {
        return fetchSection("romance")
    }

    suspend fun fetchAdventureMovies(): List<Movie> {
        return fetchSection("adventure")
    }

    suspend fun fetchAnime(): List<Movie> {
        return fetchSection("anime")
    }

    suspend fun fetchKDrama(): List<Movie> {
        return fetchSection("kdrama")
    }

    suspend fun fetchCDrama(): List<Movie> {
        return fetchSection("cdrama")
    }

    suspend fun fetchTurkishDrama(): List<Movie> {
        return fetchSection("turkish")
    }

    suspend fun fetchSADrama(): List<Movie> {
        return fetchSection("sadrama")
    }

    suspend fun fetchBlackShows(): List<Movie> {
        return fetchSection("blackshows")
    }

    suspend fun fetchHotShorts(): List<Movie> {
        return fetchSection("hot-shorts")
    }

    suspend fun fetchSmartStart(): List<Movie> {
        return fetchSection("smartstart")
    }

    suspend fun fetchUpcoming(): List<Movie> {
        return fetchSection("upcoming")
    }

    suspend fun fetchHot(): List<Movie> {
        return fetchSection("hot")
    }

    suspend fun fetchCinema(): List<Movie> {
        return fetchSection("cinema")
    }

    private suspend fun fetchSection(section: String): List<Movie> {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("$MOVIE_BASE/homepage/$section")
                    .get().build()
                val response = client.newCall(request).execute()
                val body = response.body?.string() ?: return@withContext emptyList<Movie>()
                val data: TrendingResponse = gson.fromJson(body, TrendingResponse::class.java)
                (data.trending ?: data.movies ?: emptyList()).map { parseMovie(it) }
            } catch (e: Exception) { e.printStackTrace(); emptyList() }
        }
    }

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
        if (releaseDate != null) return releaseDate.split("-").firstOrNull()?.toIntOrNull()
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
