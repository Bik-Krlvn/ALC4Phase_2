package com.birikorang_kelvin_proj.travelmantics

import android.app.Application
import timber.log.Timber


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}