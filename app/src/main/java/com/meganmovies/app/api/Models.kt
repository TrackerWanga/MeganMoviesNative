package com.meganmovies.app.api

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
    val description: String = ""
)

data class Banner(
    val title: String?,
    val subject_id: String?,
    val detail_path: String?,
    val type: String?,
    val image: BannerImage?,
    val source: String?
)

data class BannerImage(val url: String?)

data class BannerResponse(
    val success: Boolean?,
    val banners: List<Banner>?
)
