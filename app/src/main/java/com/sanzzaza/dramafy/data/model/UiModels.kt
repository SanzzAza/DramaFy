package com.sanzzaza.dramafy.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

// =====================================================================
// UI-facing models (clean shape for screens)
// =====================================================================

data class Drama(
    val id: String,
    val title: String,
    val cover: String,
    val introduction: String,
    val author: String,
    val episodeCount: Int,
    val playCount: Long,
    val tags: List<String>,
    val status: String,
    val isHot: Boolean,
    val isNew: Boolean,
    val isExclusive: Boolean,
    val cornerLabel: String? = null
) {
    companion object {
        /** Map a BookMallBook (or any shape with the same fields) to a Drama. */
        fun fromBookMall(b: BookMallBook): Drama {
            val playCount = b.cover_stat_infos.firstOrNull { it.stat_type == 6 }?.stat_value
                ?.let(::parseCompactNumber) ?: 0L
            val isHot = b.is_hot == "1"
            val isNew = b.is_new_book == "1"
            val isExclusive = b.is_exclusive == "1"
            val corner = when {
                isHot -> "HOT"
                isNew -> "NEW"
                isExclusive -> "EXCLUSIVE"
                b.cover_stat_infos.firstOrNull { it.stat_type == 6 }?.stat_value?.let {
                    if (it.isNotEmpty()) it
                    else null
                } != null && playCount > 0L -> null
                else -> null
            }
            val tags = parseCategoryNames(b.category_info).takeIf { it.isNotEmpty() }
                ?: b.stat_infos
            return Drama(
                id = b.book_id,
                title = b.book_name,
                cover = b.thumb_url.ifBlank { b.first_chapter_cover },
                introduction = b.abstract,
                author = b.author,
                episodeCount = b.serial_count.toIntOrNull() ?: 0,
                playCount = playCount,
                tags = tags,
                status = b.show_creation_status,
                isHot = isHot,
                isNew = isNew,
                isExclusive = isExclusive,
                cornerLabel = corner
            )
        }

        fun fromSearch(s: SearchItem): Drama = Drama(
            id = s.book_id,
            title = s.title,
            cover = s.cover,
            introduction = s.abstract,
            author = s.author,
            episodeCount = 0,
            playCount = s.read_count.toLongOrNull() ?: 0L,
            tags = emptyList(),
            status = s.status.orEmpty(),
            isHot = false,
            isNew = false,
            isExclusive = false
        )

        fun fromBookDetail(b: BookDetailResponse, intro: String? = null, episodeCount: Int? = null): Drama = Drama(
            id = b.book_id,
            title = b.title,
            cover = b.cover,
            introduction = intro ?: b.abstract,
            author = b.author,
            episodeCount = episodeCount ?: b.episode_count.toIntOrNull() ?: 0,
            playCount = b.read_count.toLongOrNull() ?: 0L,
            tags = b.categories,
            status = b.status,
            isHot = false,
            isNew = false,
            isExclusive = false
        )
    }
}

data class DramaGroup(
    val id: String,
    val title: String,
    val dramas: List<Drama>
)

data class Episode(
    val index: Int,
    val vid: String,
    val title: String,
    val cover: String,
    val duration: Long,
    val streamUrl: String?
)

data class Language(
    val code: String,
    val name: String
)

// =====================================================================
// Helpers
// =====================================================================

private val lenientJson = Json { ignoreUnknownKeys = true; isLenient = true }

internal fun parseCategoryNames(rawJson: String): List<String> {
    if (rawJson.isBlank()) return emptyList()
    return runCatching {
        val arr = lenientJson.parseToJsonElement(rawJson).jsonArray
        arr.mapNotNull { el ->
            el.jsonObject["Name"]?.jsonPrimitive?.contentOrNull
        }.filter { it.isNotBlank() }
    }.getOrDefault(emptyList())
}

internal fun parseCompactNumber(raw: String): Long {
    if (raw.isBlank()) return 0L
    val s = raw.trim().uppercase()
    val num = s.takeWhile { it.isDigit() || it == '.' }.toDoubleOrNull() ?: return 0L
    val mult = when {
        s.endsWith("B") -> 1_000_000_000.0
        s.endsWith("M") -> 1_000_000.0
        s.endsWith("K") -> 1_000.0
        else -> 1.0
    }
    return (num * mult).toLong()
}
