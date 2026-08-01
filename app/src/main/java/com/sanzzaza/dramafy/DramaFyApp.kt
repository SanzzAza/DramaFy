package com.sanzzaza.dramafy

import android.app.Application
import coil.Coil
import coil.ImageLoader
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class DramaFyApp : Application() {

    @Inject lateinit var imageLoader: ImageLoader

    override fun onCreate() {
        super.onCreate()
        // Set the Hilt-provided ImageLoader as the global default for Coil.
        // We don't enable HEIC support here — ImageUrl.cover() rewrites the
        // upstream HEIC URLs to .jpeg before they reach Coil.
        Coil.setImageLoader(imageLoader)
    }
}
