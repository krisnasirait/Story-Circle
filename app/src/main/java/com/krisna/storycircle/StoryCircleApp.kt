package com.krisna.storycircle

import android.app.Application
import com.krisna.storycircle.data.remote.ApiService
import com.krisna.storycircle.data.repository.StoryCircleRepository
import com.krisna.storycircle.presentation.viewmodel.AuthViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class StoryCircleApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@StoryCircleApp)
            modules(authModule, repositoryModule)
        }
    }

    val authModule = module {
        viewModel { AuthViewModel(get()) }
    }

    val repositoryModule = module {
        single {
            val client = OkHttpClient.Builder()
                .apply {
                    if (BuildConfig.DEBUG) {
                        val loggingInterceptor = HttpLoggingInterceptor()
                        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                        addInterceptor(loggingInterceptor)
                    }
                }
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://story-api.dicoding.dev/v1/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            StoryCircleRepository(retrofit.create(ApiService::class.java))
        }
    }


}