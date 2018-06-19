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
                            initCameraParams()
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

// 3-A : auto-focus, auto-exposure, and auto-white-balance + other niceties
private fun Camera.initCameraParams() {

    Timber.d("Previous Camera Params : ${parameters.flatten()}")

    setParameters {

        // takePicture related settings : Only works in sound on capture
        it.jpegQuality = 100

        val sceneModes = listOf(Camera.Parameters.SCENE_MODE_HDR, Camera.Parameters.SCENE_MODE_AUTO)
        sceneModes.forEach { mode ->
            if (it.supportedSceneModes?.contains(mode) == true) {
                Timber.d("Scene mode set : $mode")
                it.sceneMode = mode
                return@forEach
            }
        }

        // Basic 3-A settings

        val whiteBalance = Camera.Parameters.WHITE_BALANCE_AUTO
        if (it.supportedWhiteBalance?.contains(whiteBalance) == true) {
            Timber.d("White balance set : $whiteBalance")
            it.whiteBalance = whiteBalance
        }

        val flashMode = Camera.Parameters.FLASH_MODE_OFF
        if (parameters.supportedFlashModes?.contains(flashMode) == true) {
            Timber.d("Flash mode set : $flashMode")
            it.flashMode = flashMode
        }

        val antibanding = Camera.Parameters.ANTIBANDING_AUTO
        if (parameters.supportedAntibanding?.contains(antibanding) == true) {
            Timber.d("Antibalancing set : $antibanding")
            it.antibanding = antibanding
        }

        val colorEffect = Camera.Parameters.EFFECT_NONE
        if (parameters.supportedColorEffects?.contains(colorEffect) == true) {
            Timber.d("Color Effect set : $colorEffect")
            it.colorEffect = colorEffect
        }

        // FPS
        fun IntArray.maxFps(): Int = this[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]

        fun IntArray.minFps(): Int = this[Camera.Parameters.PREVIEW_FPS_MIN_INDEX]

        parameters.supportedPreviewFpsRange.forEach { Timber.d("MIN_FPS : ${it.minFps()}, MAX_FPS : ${it.maxFps()}") }

        val top60FpsRanges = parameters.supportedPreviewFpsRange?.filter { it.maxFps() <= 60 * 1000 }
        val targetFpsRange = top60FpsRanges?.maxWith(Comparator<IntArray> { lRange, rRange ->
            val maxFrameCompare = lRange.maxFps().compareTo(rRange.maxFps())
            return@Comparator if (maxFrameCompare != 0) {
                maxFrameCompare
            } else {
                // Screen Brightness Fix : take variable fps with lower minFPS over fixed or higher minFPS
                -1 * lRange.minFps().compareTo(rRange.minFps())
            }
        })

        if (targetFpsRange?.isNotEmpty() == true) {
            val minFps = targetFpsRange.minFps()
            val maxFps = targetFpsRange.maxFps()
            Timber.d("Fps Range set : $minFps, $maxFps")
            it.setPreviewFpsRange(minFps, maxFps)
        }

        if (parameters.isVideoStabilizationSupported) {
            Timber.d("Video Stabilization set : true")
            it.videoStabilization = true
        }

        it.resetAutoFocus()

        // TODO : Check if turning on custom parameters from parameters.flatten() improves video / picture quality(dynamic-range-control, rt-hdr etc.)
    }

    Timber.d("Custom Camera Params : ${parameters.flatten()}")
}

@AnyThread
fun Camera.resetAutoFocus() {
    setParameters {
        it.resetAutoFocus()
    }
}

@AnyThread
private fun Camera.Parameters.resetAutoFocus() {

    val targetMode: String? = arrayOf(
            Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE,
            Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO,
            Camera.Parameters.FOCUS_MODE_AUTO
    ).find { supportedFocusModes?.contains(it) == true }

    if (targetMode != null) {
        Timber.d("Focus Mode : $targetMode")
        focusMode = targetMode
    }

    if (maxNumFocusAreas > 0) {
        Timber.d("Focus Area Reset")
        focusAreas = null
    }
    if (maxNumMeteringAreas > 0) {
        Timber.d("Metering Area Reset")
        meteringAreas = null
    }
}

private fun Camera.setParameters(cachedParams: Camera.Parameters? = null, setAction: (Camera.Parameters) -> Unit) {

    val params = cachedParams ?: parameters
    setAction(params)
    parameters = params
}