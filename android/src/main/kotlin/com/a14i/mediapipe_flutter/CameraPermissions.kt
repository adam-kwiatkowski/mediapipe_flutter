package com.a14i.mediapipe_flutter

import android.Manifest.permission
import android.app.Activity
import android.content.pm.PackageManager
import androidx.annotation.VisibleForTesting
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.flutter.plugin.common.PluginRegistry.RequestPermissionsResultListener


class CameraPermissions {
    fun interface PermissionsRegistry {
        fun addListener(
            handler: RequestPermissionsResultListener
        )
    }

    fun interface ResultCallback {
        fun onResult(errorCode: String?, errorDescription: String?)
    }

    @VisibleForTesting
    var ongoing = false
    fun requestPermissions(
        activity: Activity,
        permissionsRegistry: PermissionsRegistry,
        enableAudio: Boolean,
        callback: ResultCallback
    ) {
        if (ongoing) {
            callback.onResult(
                CAMERA_PERMISSIONS_REQUEST_ONGOING, CAMERA_PERMISSIONS_REQUEST_ONGOING_MESSAGE
            )
            return
        }
        if (!hasCameraPermission(activity) || enableAudio && !hasAudioPermission(activity)) {
            permissionsRegistry.addListener(CameraRequestPermissionsListener { errorCode: String?, errorDescription: String? ->
                ongoing = false
                callback.onResult(errorCode, errorDescription)
            })
            ongoing = true
            ActivityCompat.requestPermissions(
                activity,
                if (enableAudio) arrayOf(permission.CAMERA, permission.RECORD_AUDIO) else arrayOf(
                    permission.CAMERA
                ),
                CAMERA_REQUEST_ID
            )
        } else {
            // Permissions already exist. Call the callback with success.
            callback.onResult(null, null)
        }
    }

    private fun hasCameraPermission(activity: Activity): Boolean {
        return (ContextCompat.checkSelfPermission(
            activity, permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun hasAudioPermission(activity: Activity): Boolean {
        return (ContextCompat.checkSelfPermission(
            activity, permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED)
    }

    @VisibleForTesting
    class CameraRequestPermissionsListener @VisibleForTesting constructor(
        val callback: ResultCallback
    ) : RequestPermissionsResultListener {
        // There's no way to unregister permission listeners in the v1 embedding, so we'll be called
        // duplicate times in cases where the user denies and then grants a permission. Keep track of if
        // we've responded before and bail out of handling the callback manually if this is a repeat
        // call.
        var alreadyCalled = false
        override fun onRequestPermissionsResult(
            id: Int, permissions: Array<String>, grantResults: IntArray
        ): Boolean {
            if (alreadyCalled || id != CAMERA_REQUEST_ID) {
                return false
            }
            alreadyCalled = true
            // grantResults could be empty if the permissions request with the user is interrupted
            // https://developer.android.com/reference/android/app/Activity#onRequestPermissionsResult(int,%20java.lang.String[],%20int[])
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                callback.onResult(CAMERA_ACCESS_DENIED, CAMERA_ACCESS_DENIED_MESSAGE)
            } else if (grantResults.size > 1 && grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                callback.onResult(AUDIO_ACCESS_DENIED, AUDIO_ACCESS_DENIED_MESSAGE)
            } else {
                callback.onResult(null, null)
            }
            return true
        }
    }

    companion object {
        /**
         * Camera access permission errors handled when camera is created. See `MethodChannelCamera`
         * in `camera/camera_platform_interface` for details.
         */
        private const val CAMERA_PERMISSIONS_REQUEST_ONGOING = "CameraPermissionsRequestOngoing"
        private const val CAMERA_PERMISSIONS_REQUEST_ONGOING_MESSAGE =
            "Another request is ongoing and multiple requests cannot be handled at once."
        private const val CAMERA_ACCESS_DENIED = "CameraAccessDenied"
        private const val CAMERA_ACCESS_DENIED_MESSAGE = "Camera access permission was denied."
        private const val AUDIO_ACCESS_DENIED = "AudioAccessDenied"
        private const val AUDIO_ACCESS_DENIED_MESSAGE = "Audio access permission was denied."
        private const val CAMERA_REQUEST_ID = 9796
    }
}