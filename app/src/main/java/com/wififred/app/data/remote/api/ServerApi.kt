package com.wififred.app.data.remote.api

import retrofit2.http.GET
import retrofit2.http.Url

interface ServerApi {
    /** فحص سرعة الاستجابة (يُستخدم لقياس Ping) */
    @GET
    suspend fun ping(@Url url: String): retrofit2.Response<Unit>
}
