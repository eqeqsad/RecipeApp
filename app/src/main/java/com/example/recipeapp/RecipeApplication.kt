package com.example.recipeapp

import android.app.Application
import com.example.recipeapp.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class RecipeApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@RecipeApplication)
            modules(appModule)
        }
    }
}
