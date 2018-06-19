package com.hpcnt.sensorchecker

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import com.hpcnt.sensorchecker.extensions.getSystemService

class SensorView(applicationContext: Context) : BaseView(applicationContext) {

    private val sensorManager = applicationContext.getSystemService<SensorManager>()
    private val accelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
    private val rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    override fun trigger() {
        super.trigger()
        val accelAvailable = availability(accelerationSensor != null)
        val gravityAvailable = availability(gravitySensor != null)
        val rotationVectorAvailable = availability(rotationVectorSensor != null)
        val items = listOf(
                ContentItem("Acceleration Sensor", accelAvailable),
                ContentItem("Gravity Sensor", gravityAvailable),
                ContentItem("RotationVector Sensor", rotationVectorAvailable)
        )

        output.postValue(items)
    }
}
