package com.krisna.storycircle.data.remote

import com.krisna.storycircle.data.model.request.LoginUserRequestBody
import com.krisna.storycircle.data.model.request.RegisterRequestBody
import com.krisna.storycircle.data.model.response.addstory.AddNewStoryResponse
import com.krisna.storycircle.data.model.response.login.LoginResponse
import com.krisna.storycircle.data.model.response.register.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @POST("register")
    suspend fun registerUser(
        @Body body: RegisterRequestBody
    ): Response<RegisterResponse>

    @POST("login")
    suspend fun loginUser(
        @Body body: LoginUserRequestBody
    ): Response<LoginResponse>

    @Multipart
    @POST("stories")
    suspend fun addStory(
        @Header("Authorization") authorization: String,
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?,
    ): Response<AddNewStoryResponse>

}