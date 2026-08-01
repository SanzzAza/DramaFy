package com.sanzzaza.dramafy.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val bookId: String,
    val title: String,
    val cover: String?,
    val introduction: String?,
    val tagsCsv: String,
    val author: String?,
    val episodeCount: Int,
    val addedAt: Long = System.currentTimeMillis()
)
