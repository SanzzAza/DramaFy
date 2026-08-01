package com.sanzzaza.dramafy.data.repository

import com.sanzzaza.dramafy.data.api.MeloloApi
import com.sanzzaza.dramafy.data.local.BookmarkDao
import com.sanzzaza.dramafy.data.local.BookmarkEntity
import com.sanzzaza.dramafy.data.model.BookDetailDto
import com.sanzzaza.dramafy.data.model.BookMallResponse
import com.sanzzaza.dramafy.data.model.BookMallTabsResponse
import com.sanzzaza.dramafy.data.model.LanguageDto
import com.sanzzaza.dramafy.data.model.MultiVideoResponse
import com.sanzzaza.dramafy.data.model.SearchItemDto
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
    suspend fun languages(): Result<List<LanguageDto>> = runCatching { api.getLanguages() }

    suspend fun search(q: String, lang: String, offset: Int = 0, limit: Int = 30): Result<SearchResponse> =
        runCatching { api.search(q, lang, limit, offset) }

    suspend fun suggest(q: String, lang: String): Result<SuggestResponse> =
        runCatching { api.suggest(q, lang) }

    suspend fun bookMall(lang: String): Result<BookMallResponse> =
        runCatching { api.getBookMall(lang) }

    suspend fun bookMallTabs(gender: Int, lang: String): Result<BookMallTabsResponse> =
        runCatching { api.getBookMallTabs(gender, lang) }

    suspend fun book(id: String, lang: String): Result<BookDetailDto> =
        runCatching { api.getBook(id, lang) }

    suspend fun series(id: String, lang: String): Result<SeriesResponse> =
        runCatching { api.getSeries(id, lang) }

    suspend fun multiVideo(id: String, lang: String): Result<MultiVideoResponse> =
        runCatching { api.getMultiVideo(id, lang) }

    // Bookmarks
    fun observeBookmarks() = bookmarkDao.observeAll()
    fun observeIsBookmarked(id: String) = bookmarkDao.observeIsBookmarked(id)

    private fun SearchItemDto.toEntity() = BookmarkEntity(
        bookId = id,
        title = title,
        cover = cover,
        introduction = introduction,
        tagsCsv = tags.joinToString(","),
        author = author,
        episodeCount = episodeCount
    )

    suspend fun addBookmark(item: SearchItemDto) = bookmarkDao.upsert(item.toEntity())
    suspend fun removeBookmark(id: String) = bookmarkDao.delete(id)
}
