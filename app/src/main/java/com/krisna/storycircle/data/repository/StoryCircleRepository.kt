package com.krisna.storycircle.data.repository

import com.krisna.storycircle.data.model.request.RegisterRequestBody
import com.krisna.storycircle.data.model.response.RegisterResponse
import com.krisna.storycircle.data.remote.ApiService

class StoryCircleRepository(
    private val apiService: ApiService,
) {

    suspend fun registerUser(name: String, email: String, password: String) : RegisterResponse {
        return apiService.registerUser(RegisterRequestBody(name, email, password))
    }
}