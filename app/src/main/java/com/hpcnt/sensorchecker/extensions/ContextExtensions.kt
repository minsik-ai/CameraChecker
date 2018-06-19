package com.hpcnt.sensorchecker.extensions

import android.content.Context
import android.hardware.SensorManager
import android.os.Vibrator

inline fun <reified T> Context.getSystemService(): T {
    return when (T::class.java) {
        Vibrator::class.java -> getSystemService(Context.VIBRATOR_SERVICE)
        SensorManager::class.java -> getSystemService(Context.SENSOR_SERVICE)
        else -> throw ClassNotFoundException("System Class not supported yet")
    } as T
}