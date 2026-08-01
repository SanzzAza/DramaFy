package com.sanzzaza.dramafy.util

import java.util.Locale

object Formatters {
    fun compactNumber(value: Long): String = when {
        value >= 1_000_000_000 -> String.format(Locale.US, "%.1fB", value / 1_000_000_000.0)
        value >= 1_000_000 -> String.format(Locale.US, "%.1fM", value / 1_000_000.0)
        value >= 1_000 -> String.format(Locale.US, "%.1fK", value / 1_000.0)
        else -> value.toString()
    }

    fun duration(seconds: Long): String {
        if (seconds <= 0) return "--:--"
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return if (h > 0) String.format(Locale.US, "%d:%02d:%02d", h, m, s)
        else String.format(Locale.US, "%d:%02d", m, s)
    }

    fun tagsToList(csv: String?): List<String> =
        csv?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
}
