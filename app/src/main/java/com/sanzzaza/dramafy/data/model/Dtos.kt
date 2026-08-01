package com.sanzzaza.dramafy.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

// =====================================================================
// Raw API DTOs (mirror the upstream JSON exactly)
// =====================================================================

// -------- Languages --------
@Serializable
data class LanguagesResponse(
    val total: Int = 0,
    val languages: Map<String, String> = emptyMap()
)

// -------- Book Mall (home) --------
@Serializable
data class BookMallResponse(
    val cell: BookMallCell = BookMallCell()
)

@Serializable
data class BookMallCell(
    val algo: Int = 0,
    val book_page_type: Int = 0,
    val books: List<BookMallBook> = emptyList(),
    val cell_data: List<BookMallCellData> = emptyList(),
    val cell_style: Int = 0,
    val cell_type: Int = 0,
    val channel_id: Long = 0,
    val cid: String = "",
    val client_log_module_name: String = "",
    val english_name: String = "",
    val has_more: Boolean = false,
    @SerialName("id") val id: String = "",
    val main_index: Int = 0,
    val name: String = "",
    val operation_type: Int = 0,
    val show_type: Int = 0
)

@Serializable
data class BookMallCellData(
    val algo: Int = 0,
    val book_page_type: Int = 0,
    val books: List<BookMallBook> = emptyList(),
    val cell_dislike_type: Int = 0,
    val channel_id: Long = 0,
    val client_log_module_name: String = "",
    val english_name: String = "",
    @SerialName("id") val id: String = "",
    val name: String = "",
    val operation_type: Int = 0,
    val show_type: Int = 0,
    val use_recommend: Boolean = false
)

@Serializable
data class BookMallBook(
    val abstract: String = "",
    val age_gate: String = "0",
    val author: String = "",
    val book_id: String = "",
    val book_name: String = "",
    val book_status: String = "",
    val book_type: String = "",
    val category_info: String = "",          // JSON-string, we parse to tags
    val category_v2_ids: String = "",
    val color_dominate: String = "",
    val comic_show_type: String = "0",
    val content_type: Int = 0,
    val cover_stat_infos: List<CoverStat> = emptyList(),
    val create_time: String = "",
    val creation_status: String = "",
    val data_rate: String = "",
    val dub_type: String = "none",
    val first_chapter_cover: String = "",
    val first_chapter_item_id: String = "",
    val first_chapter_vid_index: Int = 0,
    val gender: String = "0",
    val genre: String = "",
    val in_bookshelf: String = "0",
    val is_dubbed: String = "0",
    val is_exclusive: String = "0",
    val is_hot: String = "0",
    val is_native: String = "0",
    val is_new_book: String = "0",
    val language: String = "",
    val last_chapter_index: String = "0",
    val last_publish_time: String = "",
    val media_id: String = "",
    val novel_text_type: String = "0",
    val original_language: String = "",
    val platform: String = "",
    val read_count: String = "0",
    val serial_count: String = "0",
    val show_creation_status: String = "",
    val stat_infos: List<String> = emptyList(),
    val sub_abstract: String = "",
    val thumb_url: String = "",
    val total_price: String = "",
    val tts_status: String = "0",
    val type: String = "0",
    val visibility_age_gate: String = "0",
    val word_number: String = "0"
)

@Serializable
data class CoverStat(
    val stat_data_position: Int = 0,
    val stat_type: Int = 0,
    val stat_value: String = ""
)

// -------- Search --------
@Serializable
data class SearchResponse(
    val query: String = "",
    val total: Int = 0,
    val hasMore: Boolean = false,
    val items: List<SearchItem> = emptyList()
)

@Serializable
data class SearchItem(
    val book_id: String = "",
    val title: String = "",
    val author: String = "",
    val abstract: String = "",
    val cover: String = "",
    val read_count: String = "0",
    val source: String = "",
    val status: String? = null,
    val age_gate: String? = null,
    val language: String? = null
)

@Serializable
data class SuggestResponse(
    val is_sug_style_better: Boolean = false,
    val query_key: String = "",
    val query_result: List<String> = emptyList()
)

// -------- Book detail --------
@Serializable
data class BookDetailResponse(
    val book_id: String = "",
    val title: String = "",
    val author: String = "",
    val abstract: String = "",
    val cover: String = "",
    val status: String = "0",
    val episode_count: String = "0",
    val age_gate: String = "0",
    val read_count: String = "0",
    val language: String = "",
    val gender: String = "0",
    val categories: List<String> = emptyList(),
    val first_episode: String = "",
    val last_episode: String = "",
    val extra: JsonObject? = null
)

// -------- Series (chapters list) --------
@Serializable
data class SeriesResponse(
    val series: SeriesInfo = SeriesInfo(),
    val episodes: List<SeriesEpisode> = emptyList()
)

@Serializable
data class SeriesInfo(
    val series_id: Long = 0,
    val title: String = "",
    val intro: String = "",
    val episode_count: Int = 0,
    val episode_text: String = "",
    val play_count: Long = 0,
    val cover: String = "",
    val status: Int = 0,
    val is_vip: Boolean = false,
    val vip_status: Int = 0,
    val has_subscribed: Boolean = false
)

@Serializable
data class SeriesEpisode(
    val index: Int = 0,
    val vid: String = "",
    val duration: Long = 0,
    val likes: Long = 0,
    val cover: String = "",
    val vertical: Boolean = true,
    val disclaimer: String? = null,
    val need_unlock: Boolean = false,
    val trial_duration: Int = 0,
    val pay_info: JsonObject? = null,
    val is_paid: Boolean = false,
    val disable_play: Boolean = false,
    // multi-video adds:
    val stream_url: String? = null
)

// -------- Book Mall Tabs --------
@Serializable
data class BookMallTabsResponse(
    val book_tab_infos: List<BookTab> = emptyList()
)

@Serializable
data class BookTab(
    val bottom_unlimited: Boolean = false,
    val cells: List<TabCell> = emptyList()
)

@Serializable
data class TabCell(
    val algo: Int = 0,
    val book_page_type: Int = 0,
    val books: List<BookMallBook> = emptyList(),
    val categories: List<TabCategory> = emptyList(),
    val name: String = ""
)

@Serializable
data class TabCategory(
    val abstract: String = "",
    val back_image: String = "",
    val book_number: Int = 0,
    val dim_type: Int = 0,
    val icon: String = "",
    val id: String = "",
    val large_icon: String = "",
    val mid_pid: String = "",
    val name: String = "",
    val starling_key: String = "",
    val tag: String = "",
    val top: List<String> = emptyList()
)
