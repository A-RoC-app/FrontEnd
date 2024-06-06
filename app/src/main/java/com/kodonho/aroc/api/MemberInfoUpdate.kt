package com.kodonho.aroc.api

import com.kodonho.aroc.dto.MemberInfoUpdateDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.Path

interface MemberInfoUpdate {
    @PATCH("/member/{email}")
    fun updateMemberInfo(
        @Path("email") email: String,
        @Body memberInfoUpdateDto: MemberInfoUpdateDto
    ): Call<MemberInfoUpdateDto>
}
