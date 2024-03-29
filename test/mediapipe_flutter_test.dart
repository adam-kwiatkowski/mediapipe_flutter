import 'package:flutter_test/flutter_test.dart';
import 'package:mediapipe_flutter/detection.dart';
import 'package:mediapipe_flutter/mediapipe_flutter.dart';
import 'package:mediapipe_flutter/mediapipe_flutter_platform_interface.dart';
import 'package:mediapipe_flutter/mediapipe_flutter_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockMediapipeFlutterPlatform
    with MockPlatformInterfaceMixin
    implements MediapipeFlutterPlatform {

  // @override
  // Future<String?> getPlatformVersion() => Future.value('42');

  @override
  Future<String?> initCamera() {
    // TODO: implement initCamera
    throw UnimplementedError();
  }

  @override
  Future<String?> initModel(String modelPath) {
    // TODO: implement initModel
    throw UnimplementedError();
  }

  @override
  // TODO: implement output
  Stream<ResultBundle?> get output => throw UnimplementedError();
}

void main() {
  final MediapipeFlutterPlatform initialPlatform = MediapipeFlutterPlatform.instance;

  test('$MethodChannelMediapipeFlutter is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelMediapipeFlutter>());
  });

  test('getPlatformVersion', () async {
    MediapipeFlutter mediapipeFlutterPlugin = MediapipeFlutter();
    MockMediapipeFlutterPlatform fakePlatform = MockMediapipeFlutterPlatform();
    MediapipeFlutterPlatform.instance = fakePlatform;

    // expect(await mediapipeFlutterPlugin.getPlatformVersion(), '42');
  });
}
