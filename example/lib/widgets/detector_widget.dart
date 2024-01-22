import 'dart:async';

import 'package:flutter/material.dart';
import 'package:camera/camera.dart';
import 'package:mediapipe_flutter/detection.dart';
import 'package:mediapipe_flutter/mediapipe_flutter.dart';

import 'bounding_boxes.dart';

class DetectorWidget extends StatefulWidget {
  const DetectorWidget({super.key});

  @override
  State<DetectorWidget> createState() => _DetectorWidgetState();
}

class _DetectorWidgetState extends State<DetectorWidget>
    with WidgetsBindingObserver {
  late List<CameraDescription> cameras;
  CameraController? _cameraController;
  CameraController get _controller => _cameraController!;

  StreamSubscription? _subscription;
  final MediapipeFlutter _mediapipeFlutter = MediapipeFlutter();
  ResultBundle? resultBundle;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
    _initStateAsync();
  }

  void _initStateAsync() async {
    _initializeCamera();
    _subscription = _mediapipeFlutter.output.listen((ResultBundle? result) {
      setState(() {
        resultBundle = result;
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    if (_cameraController == null || !_controller.value.isInitialized) {
      return const SizedBox.shrink();
    }

    return Stack(
      children: [
        BoundingBoxes(
            bundle: resultBundle!,
            child: CameraPreview(_controller)),
      ],
    );
  }

  void _initializeCamera() async {
    cameras = await availableCameras();
    _cameraController = CameraController(cameras[0], ResolutionPreset.max)
      ..initialize();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) async {
    switch (state) {
      case AppLifecycleState.inactive:
        _cameraController?.stopImageStream();
        _subscription?.cancel();
        break;
      case AppLifecycleState.resumed:
        _initStateAsync();
        break;
      default:
    }
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    _cameraController?.dispose();
    _subscription?.cancel();
    super.dispose();
  }
}