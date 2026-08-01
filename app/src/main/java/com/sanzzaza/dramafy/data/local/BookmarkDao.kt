package com.sanzzaza.dramafy.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY addedAt DESC")
    fun observeAll(): Flow<List<BookmarkEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE bookId = :id)")
    fun observeIsBookmarked(id: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE bookId = :id")
    suspend fun delete(id: String)
}
