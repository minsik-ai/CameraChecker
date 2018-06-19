package com.hpcnt.sensorchecker.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.SensorManager
import android.os.Vibrator
import android.view.LayoutInflater
import timber.log.Timber

inline fun <reified T : Activity> Context.openActivity() {
    this.startActivity(Intent(this, T::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
}

fun Context.getLayoutInflater(): LayoutInflater {
    return getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
}

inline fun <reified T> Context.getSystemService(): T {
    return when (T::class.java) {
        Vibrator::class.java -> getSystemService(Context.VIBRATOR_SERVICE)
        SensorManager::class.java -> getSystemService(Context.SENSOR_SERVICE)
        else -> throw ClassNotFoundException("System Class not supported yet")
    } as T
}