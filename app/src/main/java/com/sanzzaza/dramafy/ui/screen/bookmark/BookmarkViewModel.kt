package com.sanzzaza.dramafy.ui.screen.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanzzaza.dramafy.data.local.BookmarkEntity
import com.sanzzaza.dramafy.data.model.Drama
import com.sanzzaza.dramafy.data.repository.DramaRepository
import com.sanzzaza.dramafy.util.Formatters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val repository: DramaRepository
) : ViewModel() {

    val bookmarks: StateFlow<List<Drama>> = repository.observeBookmarks()
        .map { list -> list.map { it.toDrama() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun remove(id: String) {
        viewModelScope.launch { repository.removeBookmark(id) }
    }

    private fun BookmarkEntity.toDrama() = Drama(
        id = bookId,
        title = title,
        cover = cover.orEmpty(),
        introduction = introduction.orEmpty(),
        author = author.orEmpty(),
        episodeCount = episodeCount,
        playCount = playCount,
        tags = Formatters.tagsToList(tagsCsv),
        status = "",
        isHot = false,
        isNew = false,
        isExclusive = false
    )
}
