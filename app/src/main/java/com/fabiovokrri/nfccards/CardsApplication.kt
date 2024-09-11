package com.fabiovokrri.nfccards

import android.app.Application

class CardsApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}