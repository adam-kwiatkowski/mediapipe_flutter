import 'dart:convert';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:mediapipe_flutter/detection.dart';

import 'mediapipe_flutter_platform_interface.dart';

/// An implementation of [MediapipeFlutterPlatform] that uses method channels.
class MethodChannelMediapipeFlutter extends MediapipeFlutterPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('com.a14i.mediapipe_flutter');

  @visibleForTesting
  final eventChannel = const EventChannel('com.a14i.mediapipe_flutter/results');

  @override
  Future<String?> initCamera() async {
    final version = await methodChannel.invokeMethod<String>('initCamera');
    return version;
  }

  @override
  Future<String?> initModel(String modelPath) async {
    final version = await methodChannel.invokeMethod<String>('initObjectDetector', {'modelPath': modelPath});
    return version;
  }

  @override
  Stream<ResultBundle?> get output {
    return eventChannel.receiveBroadcastStream().map((event) {
      final s = event as String;
      final o = jsonDecode(s);
      final bundle = ResultBundle.fromMap(o);
      return bundle;
    });
  }
}
