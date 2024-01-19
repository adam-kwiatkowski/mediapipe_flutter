package com.a14i.mediapipe_flutter

import android.app.Activity
import androidx.lifecycle.LifecycleOwner
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterAssets
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterPluginBinding
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.PluginRegistry.RequestPermissionsResultListener

/** MediapipeFlutterPlugin */
class MediapipeFlutterPlugin : FlutterPlugin, ActivityAware {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private lateinit var flutterPluginBinding: FlutterPluginBinding
    private lateinit var flutterAssets: FlutterAssets
    private var methodCallHandler: MethodCallHandlerImpl? = null

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPluginBinding) {
        this.flutterPluginBinding = flutterPluginBinding
        this.flutterAssets = flutterPluginBinding.flutterAssets
    }

    override fun onDetachedFromEngine(binding: FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        val listenerPredicate: (RequestPermissionsResultListener) -> Unit =
            binding::addRequestPermissionsResultListener

        val activity: Activity = binding.activity

        startListening(
            binding.activity, flutterPluginBinding.binaryMessenger, listenerPredicate, flutterAssets
        )

        if (methodCallHandler != null) {
            if (activity is LifecycleOwner) {
                methodCallHandler!!.setLifecycleOwner(activity as LifecycleOwner)
            } else {
                val proxyLifecycleProvider = ProxyLifecycleProvider(activity)
                methodCallHandler!!.setLifecycleOwner(proxyLifecycleProvider)
            }
        }
    }

    override fun onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity()
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        onAttachedToActivity(binding)
    }

    override fun onDetachedFromActivity() {
        if (methodCallHandler != null) {
            methodCallHandler!!.stopListening()
            methodCallHandler = null
        }
    }

    private fun startListening(
        activity: Activity,
        messenger: BinaryMessenger,
        permissionRegistry: CameraPermissions.PermissionsRegistry,
        flutterAssets: FlutterAssets
    ) {
        methodCallHandler = MethodCallHandlerImpl(
            activity, messenger, CameraPermissions(), permissionRegistry, flutterAssets
        )
    }
}
