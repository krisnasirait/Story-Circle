package com.krisna.storycircle

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class StoryCircleApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@StoryCircleApp)
            modules(appModule)
        }
    }


    private val appModule = module {

    }
}