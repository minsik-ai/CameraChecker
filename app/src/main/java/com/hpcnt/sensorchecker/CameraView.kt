package com.hpcnt.sensorchecker

import android.content.Context
import com.hpcnt.sensorchecker.camera.CameraExtensions

class CameraView(applicationContext: Context, private val facing: CameraExtensions.FACING) : BaseView(applicationContext) {

    private val cameraInfo = try {
        CameraExtensions.openCamera(facing)
    } catch (e: RuntimeException) {
        Pair(null, null)
    }

    override fun trigger() {
        super.trigger()

        val (camera, orientation) = cameraInfo

        val cameraAvailable = availability(camera != null)

        val params = camera?.parameters

        val supportedSceneModes = params?.supportedSceneModes?.joinToString() ?: "NONE"

        val supportedWhiteBalance = params?.supportedWhiteBalance?.joinToString() ?: "NONE"

        val supportedFlashModes = params?.supportedFlashModes?.joinToString() ?: "NONE"

        val supportedAntiBanding = params?.supportedAntibanding?.joinToString() ?: "NONE"

        val supportedColorEffects = params?.supportedColorEffects?.joinToString() ?: "NONE"

        val supportedPreviewFpsRange = params?.supportedPreviewFpsRange?.joinToString() ?: "NONE"

        val isVideoStabilizationSupported = availability(params?.isVideoStabilizationSupported ?: false)

        val supportedFocusModes = params?.supportedFocusModes?.joinToString() ?: "NONE"

        val maxNumFocusAreas = params?.maxNumFocusAreas.toString()

        val maxNumMeteringAreas = params?.maxNumMeteringAreas.toString()

        val isZoomSupported = availability(params?.isZoomSupported ?: false)

        val items = listOf(
                ContentItem("Camera Available", cameraAvailable),
                ContentItem("Camera Rotation", orientation.toString()),
                ContentItem("Supported Scene Modes", supportedSceneModes),
                ContentItem("Supported White Balance", supportedWhiteBalance),
                ContentItem("Supported Flash Modes", supportedFlashModes),
                ContentItem("Supported Anti Banding", supportedAntiBanding),
                ContentItem("Supported Color Effects", supportedColorEffects),
                ContentItem("Supported Preview Fps", supportedPreviewFpsRange),
                ContentItem("Video Stabilization Support", isVideoStabilizationSupported),
                ContentItem("Supported Focus Modes", supportedFocusModes),
                ContentItem("Max Num Focus", maxNumFocusAreas),
                ContentItem("Max Num Metering", maxNumMeteringAreas),
                ContentItem("is Zoom Supported", isZoomSupported)
        )

        output.postValue(items)
    }
}
