package com.krisna.storycircle.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.krisna.storycircle.StoryCircleApp
import com.krisna.storycircle.data.model.request.LoginUserRequestBody
import com.krisna.storycircle.data.model.request.RegisterRequestBody
import com.krisna.storycircle.data.model.response.addstory.AddNewStoryResponse
import com.krisna.storycircle.data.model.response.allstory.AllStoriesResponse
import com.krisna.storycircle.data.model.response.allstory.Story
import com.krisna.storycircle.data.model.response.detailstory.StoryDetailResponse
import com.krisna.storycircle.data.model.response.login.LoginResponse
import com.krisna.storycircle.data.model.response.register.RegisterResponse
import com.krisna.storycircle.data.remote.ApiService
import com.krisna.storycircle.data.repository.paging.AllStoriesPagingSource
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File


class StoryCircleRepository(
    private val apiService: ApiService,
) {

    private val sharedPreferences =
        StoryCircleApp.context.getSharedPreferences("credentials", Context.MODE_PRIVATE)

    private fun getBearerToken(): String {
        return sharedPreferences?.getString("bearerToken", "") ?: ""
    }

    private val token = "Bearer ${getBearerToken()}"

    suspend fun registerUser(registerRequestBody: RegisterRequestBody) : Response<RegisterResponse> {
        return apiService.registerUser(registerRequestBody)
    }

    suspend fun loginUser(loginUserRequestBody: LoginUserRequestBody) : Response<LoginResponse> {
        return apiService.loginUser(loginUserRequestBody)
    }

    suspend fun addNewStory(description: String, photo: File, lat: Double?, lon: Double?): Response<AddNewStoryResponse> {
        val descriptionRequestBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
        val photoRequestBody = photo.asRequestBody("image/*".toMediaTypeOrNull())
        val photoPart = MultipartBody.Part.createFormData("photo", photo.name, photoRequestBody)
        val latRequestBody = lat?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
        val lonRequestBody = lon?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
        return apiService.addStory(token, descriptionRequestBody, photoPart, latRequestBody, lonRequestBody)
    }

    suspend fun getAllStories(page: Int?, size: Int?, location: Int?): Response<AllStoriesResponse> {
        return apiService.getAllStories(token, page, size, location)
    }

    suspend fun getStoryDetail(storyId: String): Response<StoryDetailResponse> {
        return apiService.getStoryDetail(token, storyId)
    }

    fun getStoryPaging(): LiveData<PagingData<Story>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                AllStoriesPagingSource(apiService, null)
            }
        ).liveData
    }

}