package com.krisna.storycircle

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.krisna.storycircle.data.remote.ApiService
import com.krisna.storycircle.data.remote.RetrofitClient
import com.krisna.storycircle.data.repository.StoryCircleRepository
import com.krisna.storycircle.presentation.viewmodel.AuthViewModel
import com.krisna.storycircle.presentation.viewmodel.StoryViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class StoryCircleApp : Application() {
    override fun onCreate() {
        super.onCreate()

        context = applicationContext

        startKoin {
            androidLogger()
            androidContext(this@StoryCircleApp)
            modules(authModule, repositoryModule)
        }
    }

    private val authModule = module {
        viewModel { AuthViewModel(get()) }
        viewModel { StoryViewModel(get()) }
    }

    private val repositoryModule = module {
        single { RetrofitClient.createService<ApiService>() }
        single { StoryCircleRepository(get()) }
    }

    companion object {
        lateinit var context: Context
    }


}