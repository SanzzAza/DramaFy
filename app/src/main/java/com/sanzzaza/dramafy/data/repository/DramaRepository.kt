package com.sanzzaza.dramafy.data.repository

import com.sanzzaza.dramafy.data.api.MeloloApi
import com.sanzzaza.dramafy.data.local.BookmarkDao
import com.sanzzaza.dramafy.data.local.BookmarkEntity
import com.sanzzaza.dramafy.data.model.BookDetailResponse
import com.sanzzaza.dramafy.data.model.BookMallResponse
import com.sanzzaza.dramafy.data.model.BookMallTabsResponse
import com.sanzzaza.dramafy.data.model.Drama
import com.sanzzaza.dramafy.data.model.DramaGroup
import com.sanzzaza.dramafy.data.model.Episode
import com.sanzzaza.dramafy.data.model.Language
import com.sanzzaza.dramafy.data.model.LanguagesResponse
import com.sanzzaza.dramafy.data.model.SearchResponse
import com.sanzzaza.dramafy.data.model.SeriesResponse
import com.sanzzaza.dramafy.data.model.SuggestResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DramaRepository @Inject constructor(
    private val api: MeloloApi,
    private val bookmarkDao: BookmarkDao
) {
    // -------- API --------
    suspend fun languages(): Result<List<Language>> = runCatching {
        val resp: com.sanzzaza.dramafy.data.model.LanguagesResponse = api.getLanguages()
        val langs = resp.languages
        langs.map { (code, name) -> Language(code, name) }
            .sortedBy { it.name }
    }

    suspend fun bookMall(lang: String): Result<List<DramaGroup>> = runCatching {
        val resp: BookMallResponse = api.getBookMall(lang)
        val cell = resp.cell
        // The home feed is a flat list of cell_data entries; each has 1+ books
        // We group them by english_name / client_log_module_name for sectioned display.
        cell.cell_data
            .filter { it.books.isNotEmpty() }
            .map { g ->
                val title = g.english_name.ifBlank {
                    g.client_log_module_name.ifBlank { g.name.ifBlank { "Featured" } }
                }
                DramaGroup(
                    id = g.id.ifBlank { g.client_log_module_name.ifBlank { title } },
                    title = title,
                    dramas = g.books.map(Drama::fromBookMall)
                )
            }
    }

    suspend fun search(q: String, lang: String, offset: Int, limit: Int): Result<SearchResponse> =
        runCatching { api.search(q, lang, limit, offset) }

    suspend fun suggest(q: String, lang: String): Result<SuggestResponse> =
        runCatching { api.suggest(q, lang) }

    suspend fun bookDetail(id: String, lang: String): Result<Drama> = runCatching {
        val resp: BookDetailResponse = api.getBook(id, lang)
        Drama.fromBookDetail(resp)
    }

    suspend fun series(id: String, lang: String): Result<Pair<Drama, List<Episode>>> = runCatching {
        val resp: SeriesResponse = api.getSeries(id, lang)
        val series = resp.series
        val drama = Drama(
            id = id,
            title = series.title,
            cover = series.cover,
            introduction = series.intro,
            author = "",
            episodeCount = series.episode_count,
            playCount = series.play_count,
            tags = emptyList(),
            status = if (series.status == 1) "Ongoing" else "Completed",
            isHot = false,
            isNew = false,
            isExclusive = false
        )
        val eps = resp.episodes.map { e ->
            Episode(
                index = e.index,
                vid = e.vid,
                title = "Episode ${e.index}",
                cover = e.cover,
                duration = e.duration,
                streamUrl = null
            )
        }
        drama to eps
    }

    suspend fun multiVideo(id: String, lang: String): Result<List<Episode>> = runCatching {
        val resp: SeriesResponse = api.getMultiVideo(id, lang)
        resp.episodes.map { e ->
            Episode(
                index = e.index,
                vid = e.vid,
                title = "Episode ${e.index}",
                cover = e.cover,
                duration = e.duration,
                streamUrl = e.stream_url
            )
        }
    }

    suspend fun bookMallTabs(gender: Int, lang: String): Result<BookMallTabsResponse> =
        runCatching { api.getBookMallTabs(gender, lang) }

    // -------- Bookmarks (local) --------
    fun observeBookmarks() = bookmarkDao.observeAll()
    fun observeIsBookmarked(id: String) = bookmarkDao.observeIsBookmarked(id)

    suspend fun addBookmark(d: Drama) = bookmarkDao.upsert(d.toEntity())
    suspend fun removeBookmark(id: String) = bookmarkDao.delete(id)
}

private fun Drama.toEntity() = BookmarkEntity(
    bookId = id,
    title = title,
    cover = cover,
    introduction = introduction,
    tagsCsv = tags.joinToString(","),
    author = author,
    episodeCount = episodeCount
)
