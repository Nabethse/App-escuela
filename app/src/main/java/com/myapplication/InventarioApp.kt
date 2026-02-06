package com.myapplication

import android.app.Application
import com.myapplication.data.di.AppContainer
import com.myapplication.data.di.DefaultAppContainer

class InventarioApp : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}
