package com.a14i.mediapipe_flutter

import android.app.Activity
import android.content.Context
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import io.flutter.Log
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterAssets
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import java.util.concurrent.Executors


class MethodCallHandlerImpl(
    activity: Activity,
    messenger: BinaryMessenger,
    cameraPermissions: CameraPermissions,
    permissionsAdder: CameraPermissions.PermissionsRegistry,
    flutterAssets: FlutterAssets
) : MethodCallHandler {
    private var activity: Activity
    private var cameraPermissions: CameraPermissions
    private var permissionsAdder: CameraPermissions.PermissionsRegistry
    private var flutterAssets: FlutterAssets
    private var applicationContext: Context
    private var lifecycleOwner: LifecycleOwner? = null

    private var methodChannel: MethodChannel
    private var resultsChannel: EventChannel

    private var modelPath: String? = null

    init {
        this.activity = activity
        this.applicationContext = activity.applicationContext
        this.cameraPermissions = cameraPermissions
        this.permissionsAdder = permissionsAdder
        this.flutterAssets = flutterAssets

        methodChannel = MethodChannel(messenger, "com.a14i.mediapipe_flutter")
        resultsChannel = EventChannel(messenger, "com.a14i.mediapipe_flutter/results")

        methodChannel.setMethodCallHandler(this)
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "getPlatformVersion" -> {
                result.success("Something, idk")
            }

            "initObjectDetector" -> {
                var modelPath: String? = call.argument("modelPath")
                if (modelPath == null) result.error("Missing argument", "modelName", "")

                this.modelPath = flutterAssets.getAssetFilePathByName(modelPath!!)
            }

            "initCamera" -> {
                cameraPermissions.requestPermissions(
                    activity, permissionsAdder, false, ({ errCode: String?, errDesc: String? ->
                        if (errCode == null) {
                            try {
                                Log.d("MediaPipe Flutter", "Starting camera")
                                startCamera()
                                result.success("permission probably granted")
                            } catch (e: Exception) {
                                result.error("Exception", e.message, null)
                            }
                        } else {
                            result.error(errCode, errDesc, null)
                        }
                    })
                )
            }

            else -> {
                result.notImplemented()
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(applicationContext)

        if (modelPath == null) return

        Log.d("MediaPipe Flutter", "Start camera")

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val previewView = PreviewView(applicationContext)
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            val imageAnalyzer =
                ImageAnalysis.Builder()
                    .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                    .build()


            val backgroundExecutor = Executors.newSingleThreadExecutor()

            backgroundExecutor.execute {
                val objectDetectorHelper = ObjectDetectorHelper(
                    context = applicationContext,
                    modelPath = modelPath!!,
                    objectDetectorListener = ObjectDetectorListener(
                        onErrorCallback = { _, _ -> },
                        onResultsCallback = {
                            Log.d("MediaPipe Flutter", "Inference took: ${it.inferenceTime}")
                            Log.d("MediaPipe Flutter", "Results: ${it.results}")
                        }
                    )
                )

                Log.d("MediaPipe Flutter", "Created objectDetector")

                imageAnalyzer.setAnalyzer(
                    backgroundExecutor,
                    objectDetectorHelper::detectLivestreamFrame
                )
            }

            try {
                Log.d("MediaPipe Flutter", "unbind all")
                cameraProvider.unbindAll()
                Log.d("MediaPipe Flutter", "bind to lifecycle")
                cameraProvider.bindToLifecycle(
                    lifecycleOwner!!,
                    cameraSelector,
                    imageAnalyzer,
                )
            } catch (exc: Exception) {
                Log.e("MediaPipe Flutter", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(applicationContext))
    }

    fun stopListening() {
        methodChannel.setMethodCallHandler(null)
    }

    fun setLifecycleOwner(lifecycleOwner: LifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner
    }
}