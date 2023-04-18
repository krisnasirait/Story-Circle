package com.krisna.storycircle.data.repository

import com.krisna.storycircle.data.model.request.RegisterRequestBody
import com.krisna.storycircle.data.model.response.RegisterResponse
import com.krisna.storycircle.data.remote.ApiService
import retrofit2.Response

class StoryCircleRepository(
    private val apiService: ApiService,
) {

    suspend fun registerUser(registerRequestBody: RegisterRequestBody) : Response<RegisterResponse> {
        return apiService.registerUser(registerRequestBody)
    }
}