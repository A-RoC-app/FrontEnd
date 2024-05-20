package com.kodonho.aroc.dto

import com.google.gson.annotations.SerializedName
import java.lang.reflect.Constructor

data class MemberJoinDto(
    @SerializedName("email")
    val email: String?,

    @SerializedName("password")
    val password: String?,

    @SerializedName("userName")
    val name: String?,

    @SerializedName("phoneNumber")
    val phone: String?,

    @SerializedName("isDisable")
    val userType: Int

)