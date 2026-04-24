package com.megan.movies.api

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

data class StreamOption(val quality: String, val url: String)
data class DownloadOption(val quality: String, val sizeMb: Int, val url: String)
data class CastMember(val name: String, val character: String, val avatar: String)
data class Trailer(val url: String, val duration: Int)

data class Banner(
    val title: String?,
    val subject_id: String?,
    val detail_path: String?,
    val type: String?,
    val image: BannerImage?,
    val source: String?
)

data class BannerImage(val url: String?)

// Response models
data class TrendingResponse(
    val success: Boolean? = null,
    val total: Int? = null,
    val trending: List<MovieItem>? = null,
    val movies: List<MovieItem>? = null
)

data class BannerResponse(
    val success: Boolean? = null,
    val total: Int? = null,
    val banners: List<Banner>? = null
)

data class SearchResponse(
    val success: Boolean? = null,
    val results: List<MovieItem>? = null
)

data class MovieItem(
    val subject_id: String? = null,
    val id: String? = null,
    val detail_path: String? = null,
    val slug: String? = null,
    val title: String? = null,
    val name: String? = null,
    val year: Any? = null,
    val releaseDate: String? = null,
    val type: String? = null,
    val subjectType: Int? = null,
    val rating: Double? = null,
    val imdbRatingValue: Double? = null,
    val genres: List<String>? = null,
    val genre: String? = null,
    val poster: Any? = null,
    val poster_url: String? = null,
    val cover: Any? = null,
    val image: Any? = null,
    val backdrop: String? = null,
    val description: String? = null,
    val plot: String? = null
)

data class MovieDetailResponse(
    val success: Boolean? = null,
    val data: MovieDetailData? = null
)

data class MovieDetailData(
    val id: String? = null,
    val detail_path: String? = null,
    val title: String? = null,
    val year: Int? = null,
    val rating: Double? = null,
    val genres: List<String>? = null,
    val description: String? = null,
    val poster: PosterData? = null,
    val backdrop: BackdropData? = null,
    val streams: List<StreamData>? = null,
    val downloads: List<DownloadData>? = null,
    val cast: List<CastData>? = null,
    val trailer: TrailerData? = null
)

data class PosterData(val url: String? = null)
data class BackdropData(val url: String? = null)
data class StreamData(val quality: String? = null, val url: String? = null)
data class DownloadData(val quality: String? = null, val size_mb: Int? = null, val url: String? = null)
data class CastData(val name: String? = null, val character: String? = null, val avatar: String? = null)
data class TrailerData(val url: String? = null, val duration: Int? = null)

data class HomepageData(
    val success: Boolean? = null,
    val banners: List<Banner>? = null,
    val trending: List<Movie>? = null
)
