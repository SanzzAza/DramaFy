package com.sanzzaza.dramafy.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [BookmarkEntity::class],
    version = 1,
    exportSchema = false
)
abstract class DramaFyDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao
}
