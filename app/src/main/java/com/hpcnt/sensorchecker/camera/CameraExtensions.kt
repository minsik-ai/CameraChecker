package com.hpcnt.sensorchecker.camera

import android.hardware.Camera
import android.support.annotation.AnyThread
import timber.log.Timber

object CameraExtensions {

    enum class FACING {
        FRONT, BACK;

        operator fun not(): FACING {
            return when (this) {
                FRONT -> BACK
                BACK -> FRONT
            }
        }
    }

    /**
     * Returns Camera with Rotation in Degrees
     */
    fun openCamera(facing: FACING): Pair<Camera, Int> {

        // Camera with Rotation value.
        fun processCameraId(id: Int): Pair<Camera, Int> =
                Pair(
                        Camera.open(id).apply {
                            Timber.d("Camera Params : ${parameters.flatten()}")
                        },
                        calculateCameraRotation(id)
                )

        getCameraId(facing)?.let { id ->
            return processCameraId(id)
        }

        throw RuntimeException("Unable to Open Any Camera")
    }

    private fun getCameraId(facing: CameraExtensions.FACING): Int? {
        val info = Camera.CameraInfo()
        val numCameras = Camera.getNumberOfCameras()
        val direction =
                when (facing) {
                    FACING.FRONT -> Camera.CameraInfo.CAMERA_FACING_FRONT
                    FACING.BACK -> Camera.CameraInfo.CAMERA_FACING_BACK
                }

        for (i in 0 until numCameras) {
            Camera.getCameraInfo(i, info)
            if (info.facing == direction) {
                return i
            }
        }
        return null
    }

    private fun calculateCameraRotation(cameraId: Int): Int {
        val info = android.hardware.Camera.CameraInfo()
        android.hardware.Camera.getCameraInfo(cameraId, info)

        val result: Int = info.orientation

        Timber.d("Rotation : $result")

        return result
    }
}