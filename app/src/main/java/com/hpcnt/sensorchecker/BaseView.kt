package com.hpcnt.sensorchecker

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import timber.log.Timber

abstract class BaseView(protected val applicationContext: Context) {

    val output: MutableLiveData<List<ContentItem>> = MutableLiveData()

    open fun trigger() {
        Timber.d("${this.javaClass.name} trigger")
    }

    open fun release() {
        Timber.d("${this.javaClass.name} clear")
    }

    protected fun availability(isAvailable: Boolean): String = if (isAvailable) "TRUE" else "FALSE"
}