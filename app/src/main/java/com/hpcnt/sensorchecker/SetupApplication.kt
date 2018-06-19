package com.hpcnt.sensorchecker

import android.app.Application
import timber.log.Timber

@Suppress("unused")
class SetupApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }
}