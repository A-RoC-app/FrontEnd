package com.kodonho.aroc.api

import com.kodonho.aroc.dto.MemberJoinDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface MemberJoin {

    // 회원가입
    @POST("/member")
    fun signup(@Body member : MemberJoinDto) : Call<MemberJoinDto>
}