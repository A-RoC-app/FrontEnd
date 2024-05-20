package com.kodonho.aroc.RetrofitInstance

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AuthInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // 여기에 헤더 추가와 같은 인증 로직을 구현합니다.
        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer your_token_here")
            .build()

        return chain.proceed(authenticatedRequest)
    }
}
