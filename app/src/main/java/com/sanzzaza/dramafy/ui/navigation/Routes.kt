package com.sanzzaza.dramafy.ui.navigation

object Routes {
    const val HOME = "home"
    const val SEARCH = "search"
    const val BOOKMARKS = "bookmarks"
    const val LANGUAGE = "language"
    const val SETTINGS = "settings"
    const val DETAIL = "detail/{bookId}"
    const val PLAYER = "player/{bookId}/{episodeIndex}"

    fun detail(bookId: String) = "detail/$bookId"
    fun player(bookId: String, episodeIndex: Int) = "player/$bookId/$episodeIndex"
}
