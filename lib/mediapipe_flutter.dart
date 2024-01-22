
import 'package:mediapipe_flutter/detection.dart';

import 'mediapipe_flutter_platform_interface.dart';

class MediapipeFlutter {
  Future<String?> initModel(String modelPath) {
    return MediapipeFlutterPlatform.instance.initModel(modelPath);
  }

  Future<String?> initCamera() {
    return MediapipeFlutterPlatform.instance.initCamera();
  }

  Stream<ResultBundle?> get output {
    return MediapipeFlutterPlatform.instance.output;
  }
}
