package com.sanzzaza.dramafy.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

// ---------------- Languages ----------------
@Serializable
data class LanguageDto(
    val code: String = "",
    val name: String = "",
    val native: String? = null
)

// ---------------- Search ----------------
@Serializable
data class SearchResponse(
    val hasMore: Boolean = false,
    val items: List<SearchItemDto> = emptyList(),
    val total: Int = 0
)

@Serializable
data class SearchItemDto(
    val id: String = "",
    val title: String = "",
    val cover: String? = null,
    val introduction: String? = null,
    val tags: List<String> = emptyList(),
    val author: String? = null,
    val episodeCount: Int = 0,
    val playCount: Long = 0,
    val rating: Double = 0.0,
    val corner: String? = null,
    val cornerColor: String? = null
)

@Serializable
data class SuggestResponse(
    val suggestions: List<String> = emptyList()
)

// ---------------- Book Mall (home feed) ----------------
@Serializable
data class BookMallResponse(
    val banners: List<BannerDto> = emptyList(),
    val categories: List<CategoryDto> = emptyList(),
    val groups: List<BookGroupDto> = emptyList()
)

@Serializable
data class BannerDto(
    val id: String = "",
    val bookId: String = "",
    val title: String = "",
    val image: String = "",
    val link: String? = null
)

@Serializable
data class CategoryDto(
    val id: String = "",
    val name: String = "",
    val icon: String? = null
)

@Serializable
data class BookGroupDto(
    val id: String = "",
    val name: String = "",
    val type: String = "",
    val books: List<SearchItemDto> = emptyList()
)

@Serializable
data class BookMallTabsResponse(
    val tabs: List<CategoryDto> = emptyList()
)

// ---------------- Book / Series detail ----------------
@Serializable
data class BookDetailDto(
    val id: String = "",
    val title: String = "",
    val cover: String? = null,
    val introduction: String = "",
    val tags: List<String> = emptyList(),
    val author: String? = null,
    val episodeCount: Int = 0,
    val playCount: Long = 0,
    val rating: Double = 0.0,
    val category: String? = null,
    val status: String? = null,
    val episodes: List<EpisodeDto> = emptyList()
)

@Serializable
data class SeriesResponse(
    val book: BookDetailDto = BookDetailDto(),
    val related: List<SearchItemDto> = emptyList()
)

@Serializable
data class EpisodeDto(
    val id: String = "",
    val index: Int = 0,
    val title: String = "",
    val cover: String? = null,
    val duration: Long = 0,
    val isFree: Boolean = true,
    val coin: Int = 0
)

// ---------------- Multi video (stream info) ----------------
@Serializable
data class MultiVideoResponse(
    val videoId: String = "",
    val videoList: List<VideoSourceDto> = emptyList()
)

@Serializable
data class VideoSourceDto(
    val quality: String = "",
    val url: String = "",
    val format: String = "mp4",
    val duration: Long = 0,
    val size: Long = 0
)

// ---------------- Generic (fallback) ----------------
@Serializable
data class GenericResponse(
    val code: Int = 0,
    val message: String? = null,
    val data: JsonObject? = null
)
