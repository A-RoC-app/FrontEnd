package com.kodonho.aroc.api

import com.kodonho.aroc.dto.MemberViewDto
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface MemberView {
    //정보조회
    @GET("/member/{email}")
    fun getMemberInfo(
        @Path("email") email: String,
    ): Call<MemberViewDto>

    //탈퇴
    @DELETE("/member/{email}")
    fun deleteMemberInfo(
        @Path("email") email:String
    ): Call<Void>
}
