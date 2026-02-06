package com.myapplication

import android.app.Application
import com.myapplication.core.di.AppContainer
import com.myapplication.core.di.DefaultAppContainer

class EscuelaApp : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}
