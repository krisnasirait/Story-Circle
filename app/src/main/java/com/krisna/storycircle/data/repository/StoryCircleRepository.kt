package com.krisna.storycircle.data.repository

import com.krisna.storycircle.data.model.request.LoginUserRequestBody
import com.krisna.storycircle.data.model.request.RegisterRequestBody
import com.krisna.storycircle.data.model.response.addstory.AddNewStoryResponse
import com.krisna.storycircle.data.model.response.allstory.AllStoriesResponse
import com.krisna.storycircle.data.model.response.login.LoginResponse
import com.krisna.storycircle.data.model.response.register.RegisterResponse
import com.krisna.storycircle.data.remote.ApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File

class StoryCircleRepository(
    private val apiService: ApiService,
) {

    suspend fun registerUser(registerRequestBody: RegisterRequestBody) : Response<RegisterResponse> {
        return apiService.registerUser(registerRequestBody)
    }

    suspend fun loginUser(loginUserRequestBody: LoginUserRequestBody) : Response<LoginResponse> {
        return apiService.loginUser(loginUserRequestBody)
    }

    suspend fun addNewStory(token: String, description: String, photo: File, lat: Double?, lon: Double?): Response<AddNewStoryResponse> {
        val descriptionRequestBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
        val photoRequestBody = photo.asRequestBody("image/*".toMediaTypeOrNull())
        val photoPart = MultipartBody.Part.createFormData("photo", photo.name, photoRequestBody)
        val latRequestBody = lat?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
        val lonRequestBody = lon?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())

        return apiService.addStory("Bearer $token", descriptionRequestBody, photoPart, latRequestBody, lonRequestBody)
    }

    suspend fun getAllStories(token: String, page: Int?, size: Int?, location: Int?): Response<AllStoriesResponse> {
        return apiService.getAllStories("Bearer $token", page, size, location)
    }

}