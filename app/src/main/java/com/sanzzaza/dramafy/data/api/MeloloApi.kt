package com.sanzzaza.dramafy.data.api

import com.sanzzaza.dramafy.data.model.BookDetailDto
import com.sanzzaza.dramafy.data.model.BookMallResponse
import com.sanzzaza.dramafy.data.model.BookMallTabsResponse
import com.sanzzaza.dramafy.data.model.LanguageDto
import com.sanzzaza.dramafy.data.model.MultiVideoResponse
import com.sanzzaza.dramafy.data.model.SearchResponse
import com.sanzzaza.dramafy.data.model.SeriesResponse
import com.sanzzaza.dramafy.data.model.SuggestResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MeloloApi {

    @GET("v1/languages")
    suspend fun getLanguages(): List<LanguageDto>

    @GET("v1/search")
    suspend fun search(
        @Query("q") query: String,
        @Query("lang") lang: String,
        @Query("limit") limit: Int = 30,
        @Query("offset") offset: Int = 0
    ): SearchResponse

    @GET("v1/search/suggest")
    suspend fun suggest(
        @Query("q") query: String,
        @Query("lang") lang: String
    ): SuggestResponse

    @GET("v1/bookmall")
    suspend fun getBookMall(
        @Query("lang") lang: String
    ): BookMallResponse

    @GET("v1/bookmall/tabs")
    suspend fun getBookMallTabs(
        @Query("gender") gender: Int = 0,
        @Query("lang") lang: String
    ): BookMallTabsResponse

    @GET("v1/book")
    suspend fun getBook(
        @Query("id") id: String,
        @Query("lang") lang: String
    ): BookDetailDto

    @GET("v1/series")
    suspend fun getSeries(
        @Query("id") id: String,
        @Query("lang") lang: String
    ): SeriesResponse

    @GET("v1/multi-video")
    suspend fun getMultiVideo(
        @Query("id") id: String,
        @Query("lang") lang: String
    ): MultiVideoResponse
}
