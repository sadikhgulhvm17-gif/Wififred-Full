package com.wififred.app.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * اعتراض الطلبات لتسجيلها في سجل التطبيق
 */
class LoggingInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val start = System.currentTimeMillis()
        val response = chain.proceed(request)
        val duration = System.currentTimeMillis() - start
        android.util.Log.d(
            "WififredHTTP",
            "${request.method} ${request.url} -> ${response.code} (${duration}ms)"
        )
        return response
    }
}
