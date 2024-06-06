package com.kodonho.aroc.dto

import com.google.gson.annotations.SerializedName

data class MemberInfoUpdateDto (

    @SerializedName("password")
    val password: String?,

    @SerializedName("userName")
    val userName: String?,

    @SerializedName("phoneNumber")
    val phoneNumber: String?,

)