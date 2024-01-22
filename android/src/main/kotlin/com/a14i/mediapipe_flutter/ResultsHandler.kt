package com.a14i.mediapipe_flutter

import android.os.Handler
import android.os.Looper
import com.google.gson.Gson
import io.flutter.Log
import io.flutter.plugin.common.EventChannel

class ResultsHandler : EventChannel.StreamHandler, ObjectDetectorHelper.DetectorListener {
    private var eventSink: EventChannel.EventSink? = null
    private var handler = Handler(Looper.getMainLooper())
    private val gson = Gson()
    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        Log.d("MediaPipe Flutter", "onListen")
        eventSink = events
    }

    override fun onCancel(arguments: Any?) {
        Log.d("MediaPipe Flutter", "onCancel")
        eventSink = null
    }

    override fun onError(error: String, errorCode: Int) {
        Log.d("MediaPipe Flutter", "onError: $error, $errorCode")
        handler.post {
            eventSink?.error(error, null, errorCode)
        }
    }

    override fun onResults(resultBundle: ObjectDetectorHelper.ResultBundle) {
        val resultBundleJson = gson.toJson(resultBundle)

        handler.post {
            eventSink?.success(resultBundleJson)
        }
    }
}