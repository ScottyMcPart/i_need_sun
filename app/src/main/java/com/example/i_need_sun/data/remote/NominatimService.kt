package com.example.i_need_sun.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query


// --Response Model-------------
data class NominatimResult(
    @SerializedName("place_id") val placeId: Long,
    @SerializedName("display_name") val displayName: String,
    @SerializedName("lat") val lat: String,
    @SerializedName("lon") val lon: String,
) {
    //converters
    val latitude: Double get() = lat.toDouble()
    val longitude: Double get() = lon.toDouble()

    //shortened label for picker list
    val shortName: String get() = displayName
        .split(",")
        .take(2)
        .joinToString(",")
        .trim()
}

//Retrofit Interface
interface NominatimService {

    @GET("search")
    suspend fun search(
        @Query("q") query:String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 5
    ): List<NominatimResult>
}