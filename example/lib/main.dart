import 'package:flutter/material.dart';
import 'package:mediapipe_flutter/detection.dart';
import 'package:mediapipe_flutter/mediapipe_flutter.dart';
import 'package:mediapipe_flutter_example/widgets/detector_widget.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();

  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final _mediapipeFlutterPlugin = MediapipeFlutter();

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            children: [
              const Text('Flutter Mediapipe'),
              ElevatedButton(
                  onPressed: () {
                    _mediapipeFlutterPlugin.initCamera();
                  },
                  child: const Text("Init camera")),
              ElevatedButton(
                  onPressed: () {
                    _mediapipeFlutterPlugin.initModel("assets/model.tflite");
                  },
                  child: const Text("Init model")),
              // StreamBuilder<ResultBundle?>(
              //     stream: _mediapipeFlutterPlugin.output,
              //     builder: (context, snapshot) {
              //       if (snapshot.hasData) {
              //         ResultBundle resultBundle = snapshot.data!;
              //         return Text(
              //             'Result: ${resultBundle.toString()}');
              //       } else {
              //         return const Text('No data');
              //       }
              //     }),

              const DetectorWidget(),
            ],
          ),
        ),
      ),
    );
  }
}
