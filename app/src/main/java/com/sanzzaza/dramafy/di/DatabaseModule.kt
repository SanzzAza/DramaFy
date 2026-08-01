package com.sanzzaza.dramafy.di

import android.content.Context
import androidx.room.Room
import com.sanzzaza.dramafy.data.local.BookmarkDao
import com.sanzzaza.dramafy.data.local.DramaFyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DramaFyDatabase =
        Room.databaseBuilder(context, DramaFyDatabase::class.java, "dramafy.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideBookmarkDao(db: DramaFyDatabase): BookmarkDao = db.bookmarkDao()
}
