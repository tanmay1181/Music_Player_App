package com.example.sansic

import android.app.Application
import com.example.sansic.data.AppContainer
import com.example.sansic.data.AppDataContainer

class SanSicApplication: Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}