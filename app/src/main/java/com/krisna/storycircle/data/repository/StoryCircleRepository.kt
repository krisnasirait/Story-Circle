package com.krisna.storycircle.data.repository

import com.krisna.storycircle.data.model.request.LoginUserRequestBody
import com.krisna.storycircle.data.model.request.RegisterRequestBody
import com.krisna.storycircle.data.model.response.login.LoginResponse
import com.krisna.storycircle.data.model.response.register.RegisterResponse
import com.krisna.storycircle.data.remote.ApiService
import retrofit2.Response

class StoryCircleRepository(
    private val apiService: ApiService,
) {

    suspend fun registerUser(registerRequestBody: RegisterRequestBody) : Response<RegisterResponse> {
        return apiService.registerUser(registerRequestBody)
    }

    suspend fun loginUser(loginUserRequestBody: LoginUserRequestBody) : Response<LoginResponse> {
        return apiService.loginUser(loginUserRequestBody)
    }
}