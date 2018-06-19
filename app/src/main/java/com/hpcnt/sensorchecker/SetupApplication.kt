package com.hpcnt.sensorchecker

import android.app.Application
import android.net.Uri
import timber.log.Timber

class SetupApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }
}