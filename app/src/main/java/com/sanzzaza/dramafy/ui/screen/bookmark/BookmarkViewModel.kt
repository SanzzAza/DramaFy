package com.sanzzaza.dramafy.ui.screen.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanzzaza.dramafy.data.local.BookmarkEntity
import com.sanzzaza.dramafy.data.repository.DramaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BookmarkItem(
    val id: String,
    val title: String,
    val cover: String?,
    val introduction: String?,
    val tags: List<String>,
    val author: String?,
    val episodeCount: Int
)

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val repository: DramaRepository
) : ViewModel() {

    val bookmarks: StateFlow<List<BookmarkItem>> = repository.observeBookmarks()
        .map { list -> list.map { it.toItem() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun remove(id: String) {
        viewModelScope.launch { repository.removeBookmark(id) }
    }

    private fun BookmarkEntity.toItem() = BookmarkItem(
        id = bookId,
        title = title,
        cover = cover,
        introduction = introduction,
        tags = com.sanzzaza.dramafy.util.Formatters.tagsToList(tagsCsv),
        author = author,
        episodeCount = episodeCount
    )
}
