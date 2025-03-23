package com.simple.task

import android.app.Application
import com.simple.task.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class TaskApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TaskApp)
            modules(appModule)
        }
    }
}