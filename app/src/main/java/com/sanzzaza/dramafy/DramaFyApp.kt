package com.sanzzaza.dramafy

import android.app.Application
import coil.Coil
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class DramaFyApp : Application() {

    @Inject lateinit var imageLoader: ImageLoader

    override fun onCreate() {
        super.onCreate()
        // Configure Coil with a safe image loader that supports common formats
        // (.jpg, .jpeg, .png, .webp, .gif) and falls back gracefully on errors.
        // Note: we intentionally do NOT enable HEIC support — the API serves
        // .heic covers which most Android devices can't decode, and we rewrite
        // those URLs to .jpeg upstream via ImageUrl.cover().
        if (!::imageLoader.isInitialized) {
            imageLoader = defaultImageLoader()
        }
        Coil.setImageLoader(imageLoader)
    }

    private fun defaultImageLoader(): ImageLoader {
        val memCache = MemoryCache.Builder(this)
            .maxSizePercent(0.20)
            .build()
        val diskCache = DiskCache.Builder()
            .directory(cacheDir.resolve("image_cache"))
            .maxSizeBytes(120L * 1024 * 1024)
            .build()
        return ImageLoader.Builder(this)
            .memoryCache(memCache)
            .diskCache(diskCache)
            .respectCacheHeaders(false)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            .crossfade(200)
            .allowHardware(true)
            .build()
    }
}
