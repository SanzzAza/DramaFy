package com.sanzzaza.dramafy.util

/**
 * Helpers for transforming cover image URLs returned by the API.
 *
 * The drama API serves covers as .heic which Coil does not decode out of
 * the box on most devices, and many CDNs also reject Android user-agents
 * for image-resize routes. We rewrite the URL to a format that:
 *   - is widely decodable on stock Android (jpeg / webp / png)
 *   - requests a smaller size so lists scroll smoothly
 */
object ImageUrl {

    private const val FALLBACK_SIZE = "336:478" // matches API's 3:4 aspect

    /** Public entry point used everywhere a cover is rendered. */
    fun cover(raw: String?, size: String = FALLBACK_SIZE): String? {
        if (raw.isNullOrBlank()) return null
        return runCatching { rewrite(raw, size) }.getOrNull()
    }

    private fun rewrite(url: String, size: String): String {
        var out = url
        // 1) .heic / .heif are unsupported by Coil — rewrite to .jpeg
        out = out.replace(Regex("""\.hei[cf](\?|$)"""), ".jpeg$1")
        // 2) Some API image routes include "image-quality-ttk1-cp:510:727"
        //    which is a higher-resolution image. Bump it to a smaller size
        //    so list scrolling stays smooth.
        out = out.replace(Regex("""image-quality-ttk1-cp:\d+:\d+"""), "image-quality-ttk1-cp:$size")
        out = out.replace(Regex("""resize:\d+:\d+"""), "resize:$size")
        return out
    }
}
