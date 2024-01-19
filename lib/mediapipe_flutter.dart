
import 'mediapipe_flutter_platform_interface.dart';

class MediapipeFlutter {
  Future<String?> getPlatformVersion() {
    return MediapipeFlutterPlatform.instance.getPlatformVersion();
  }

  Future<String?> initModel(String modelPath) {
    return MediapipeFlutterPlatform.instance.initModel(modelPath);
  }

  Future<String?> initCamera() {
    return MediapipeFlutterPlatform.instance.initCamera();
  }
}
