package com.kodonho.aroc.api

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthService { //로그인
    @FormUrlEncoded //application/x-www-form-urlencoded 형식
    @POST("/login")
    fun Login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<Void>
}