package com.kodonho.aroc.api

import com.kodonho.aroc.dto.MapSearchDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface MapSearch {

    @GET("/path-find/search")
    fun search(
        @Query("destination") destination: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): Call<List<MapSearchDto>>
}