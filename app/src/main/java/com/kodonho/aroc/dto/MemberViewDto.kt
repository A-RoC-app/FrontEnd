package com.kodonho.aroc.dto

import com.google.gson.annotations.SerializedName

/*data class LoginResponse(
    @SerializedName("jwt") val jwt: String
)*/
data class MemberViewDto (
    @SerializedName("id")
    val id: Int,

    @SerializedName("email")
    val email: String?,

    @SerializedName("userName")
    val name: String?,

    @SerializedName("phoneNumber")
    val phone: String?,

    @SerializedName("isDisabled")
    val userType: Int
)