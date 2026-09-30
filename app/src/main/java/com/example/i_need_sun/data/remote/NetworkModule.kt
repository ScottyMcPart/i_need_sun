package com.example.i_need_sun.data.remote

import com.example.i_need_sun.data.remote.NominatimService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

private val httpClient = OkHttpClient.Builder()
    .addInterceptor { chain ->
        chain.proceed(
            chain.request().newBuilder()
                .header("User-Agent", "INeedSunApp/1.0 (android)")
                .build()
        )
    }
    .build()

//Nominate instance - base URL must end with /
val nominatimService: NominatimService = Retrofit.Builder()
    .baseUrl("https://nominatim.openstreetmap.org/")
    .client(httpClient)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(NominatimService::class.java)

