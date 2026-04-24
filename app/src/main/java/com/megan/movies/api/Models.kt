package com.megan.movies.api

// Movie model used throughout the app
data class Movie(
    val id: String,
    val slug: String,
    val title: String,
    val year: Int?,
    val type: String,
    val rating: Double?,
    val genres: List<String>,
    val poster: String,
    val backdrop: String? = null,
    val description: String = "",
    val streams: List<StreamOption> = emptyList(),
    val downloads: List<DownloadOption> = emptyList(),
    val cast: List<CastMember> = emptyList(),
    val trailer: Trailer? = null
)

data class StreamOption(
    val quality: String,
    val url: String
)

data class DownloadOption(
    val quality: String,
    val sizeMb: Int,
    val url: String
)

data class CastMember(
    val name: String,
    val character: String,
    val avatar: String
)

data class Trailer(
    val url: String,
    val duration: Int
)

// Banner model - matches actual API response
data class Banner(
    val title: String?,
    val subject_id: String?,
    val detail_path: String?,
    val type: String?,
    val image: BannerImage?,
    val source: String?
)

data class BannerImage(
    val url: String?
)

// API Response models - FIXED to match actual API structure

// Trending: { "success": true, "total": 40, "trending": [...] }
data class TrendingResponse(
    val success: Boolean?,
    val total: Int?,
    val trending: List<MovieItem>?
)

// Banners: { "success": true, "total": 11, "banners": [...] }
data class BannerResponse(
    val success: Boolean?,
    val total: Int?,
    val banners: List<Banner>?
)

// Search: { "success": true, "results": [...] }
data class SearchResponse(
    val success: Boolean?,
    val results: List<MovieItem>?
)

data class MovieItem(
    val subject_id: String?,
    val id: String?,
    val detail_path: String?,
    val slug: String?,
    val title: String?,
    val name: String?,
    val year: Any?,
    val releaseDate: String?,
    val type: String?,
    val subjectType: Int?,
    val rating: Double?,
    val imdbRatingValue: Double?,
    val genres: List<String>?,
    val genre: String?,
    val poster: Any?,
    val poster_url: String?,
    val cover: Any?,
    val image: Any?,
    val backdrop: String?,
    val description: String?,
    val plot: String?
)

data class MovieDetailResponse(
    val success: Boolean?,
    val data: MovieDetailData?
)

data class MovieDetailData(
    val id: String?,
    val detail_path: String?,
    val title: String?,
    val year: Int?,
    val rating: Double?,
    val genres: List<String>?,
    val description: String?,
    val poster: PosterData?,
    val backdrop: BackdropData?,
    val streams: List<StreamData>?,
    val downloads: List<DownloadData>?,
    val cast: List<CastData>?,
    val trailer: TrailerData?
)

data class PosterData(val url: String?)
data class BackdropData(val url: String?)
data class StreamData(val quality: String?, val url: String?)
data class DownloadData(val quality: String?, val size_mb: Int?, val url: String?)
data class CastData(val name: String?, val character: String?, val avatar: String?)
data class TrailerData(val url: String?, val duration: Int?)

data class HomepageData(
    val success: Boolean?,
    val banners: List<Banner>?,
    val trending: List<Movie>?
)

data class TrendingResponse(
    val success: Boolean?,
    val total: Int?,
    val trending: List<MovieItem>?,
    val action: List<MovieItem>? = null,
    val movies: List<MovieItem>? = null
)
