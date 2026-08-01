package com.sanzzaza.dramafy.data.api

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Adds the required Authorization header + User-Agent to every request.
 * Token supplied by the upstream API provider.
 */
class AuthInterceptor(
    private val token: String,
    private val userAgent: String = "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Mobile Safari/537.36"
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val request = original.newBuilder()
            .header("Authorization", "Bearer $token")
            .header("User-Agent", userAgent)
            .header("Accept", "application/json")
            .header("Accept-Language", "en-US,en;q=0.9")
            .build()
        return chain.proceed(request)
    }
}
