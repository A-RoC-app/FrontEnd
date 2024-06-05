package com.kodonho.aroc.dto

import com.google.gson.annotations.SerializedName

data class MapSearchDto (
    @SerializedName("destination")
    val destination: String?,

    @SerializedName("address")
    val address: String?,

    @SerializedName("lat")
    val lat: Double?,

    @SerializedName("lon")
    val lon: Double?
)

