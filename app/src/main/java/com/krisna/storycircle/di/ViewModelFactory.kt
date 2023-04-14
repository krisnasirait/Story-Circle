package com.krisna.storycircle.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krisna.storycircle.BuildConfig
import com.krisna.storycircle.data.remote.ApiService
import com.krisna.storycircle.data.repository.StoryCircleRepository
import com.krisna.storycircle.presentation.viewmodel.AuthViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val repository: StoryCircleRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    companion object {
        private const val BASE_URL = "https://story-api.dicoding.dev/v1/"

        private val client = OkHttpClient.Builder()
            .apply {
                if (BuildConfig.DEBUG) {
                    val loggingInterceptor = HttpLoggingInterceptor()
                    loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                    addInterceptor(loggingInterceptor)
                }
            }
            .build()

        private val instance: ApiService by lazy {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            retrofit.create(ApiService::class.java)
        }

        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(context: Context) = synchronized(ViewModelFactory::class.java) {
            INSTANCE ?: ViewModelFactory(
                StoryCircleRepository(instance)
            ).also { INSTANCE = it }
        }
    }
}