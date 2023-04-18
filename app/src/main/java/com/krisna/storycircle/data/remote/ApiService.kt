package com.krisna.storycircle.data.remote

import com.krisna.storycircle.data.model.request.LoginUserRequestBody
import com.krisna.storycircle.data.model.request.RegisterRequestBody
import com.krisna.storycircle.data.model.response.login.LoginResponse
import com.krisna.storycircle.data.model.response.register.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("register")
    suspend fun registerUser(
        @Body body: RegisterRequestBody
    ): Response<RegisterResponse>

    @POST("login")
    suspend fun loginUser(
        @Body body: LoginUserRequestBody
    ): Response<LoginResponse>

}