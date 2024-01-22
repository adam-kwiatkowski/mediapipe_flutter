import 'package:mediapipe_flutter/detection.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'mediapipe_flutter_method_channel.dart';

abstract class MediapipeFlutterPlatform extends PlatformInterface {
  /// Constructs a MediapipeFlutterPlatform.
  MediapipeFlutterPlatform() : super(token: _token);

  static final Object _token = Object();

  static MediapipeFlutterPlatform _instance = MethodChannelMediapipeFlutter();

  /// The default instance of [MediapipeFlutterPlatform] to use.
  ///
  /// Defaults to [MethodChannelMediapipeFlutter].
  static MediapipeFlutterPlatform get instance => _instance;

  Stream<ResultBundle?> get output => throw UnimplementedError('output has not been implemented.');

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [MediapipeFlutterPlatform] when
  /// they register themselves.
  static set instance(MediapipeFlutterPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> initCamera() {
    throw UnimplementedError('initCamera() has not been implemented.');
  }

  Future<String?> initModel(String modelPath) {
    throw UnimplementedError('initModel() has not been implemented.');
  }
}
