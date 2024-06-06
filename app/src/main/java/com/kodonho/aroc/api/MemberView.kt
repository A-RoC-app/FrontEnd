package com.kodonho.aroc.api

import com.kodonho.aroc.dto.MemberViewDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface MemberView {
    @GET("/member/{email}")
    fun getMemberInfo(
        @Path("email") email: String,
        //@Header("Authorization") token: String
    ): Call<MemberViewDto>
}
