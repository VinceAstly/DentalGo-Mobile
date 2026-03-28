package com.dentalgo.app.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Centralized Retrofit client for DentalGo Spring Boot API.
 *
 * Spring Boot app name : dentalgo
 * Database             : MongoDB Atlas (dentalgo_db)
 * Default port         : 8080
 *
 * ── URL Guide ──────────────────────────────────────────────────
 *  Android Emulator  → http://10.0.2.2:8080/
 *  Physical Device   → http://<your-PC-local-IP>:8080/
 *                      (find with: ipconfig → IPv4 Address)
 *  Production/Cloud  → https://your-deployed-url.com/
 * ───────────────────────────────────────────────────────────────
 */
object RetrofitClient {

    // ✅ Using Android Emulator localhost (Spring Boot default port 8080)
    // 👉 If using a physical device on the same WiFi, change to:
    //    private const val BASE_URL = "http://192.168.x.x:8080/"
    private const val BASE_URL = "http://10.0.2.2:8080/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
