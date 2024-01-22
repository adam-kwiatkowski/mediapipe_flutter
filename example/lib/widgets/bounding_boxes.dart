import 'package:flutter/material.dart';
import 'package:mediapipe_flutter/detection.dart';

class BoundingBoxes extends StatelessWidget {
  final Widget? child;
  final ResultBundle bundle;

  const BoundingBoxes({super.key, this.child, required this.bundle});

  @override
  Widget build(BuildContext context) {
    return CustomPaint(
      foregroundPainter: BoundingBoxesPainter(bundle),
      child: child,
    );
  }
}

class BoundingBoxesPainter extends CustomPainter {
  final ResultBundle bundle;
  final Paint _paint = Paint()
    ..color = Colors.red
    ..strokeWidth = 2
    ..style = PaintingStyle.stroke;

  BoundingBoxesPainter(this.bundle);

  @override
  void paint(Canvas canvas, Size size) {
    for (var result in bundle.results) {
      for (var detection in result.detections) {
        var rect = detection.boundingBox;
        rect = Rect.fromLTRB(
          rect.left / bundle.inputImageWidth * size.width,
          rect.top / bundle.inputImageHeight * size.height,
          rect.right / bundle.inputImageWidth * size.width,
          rect.bottom / bundle.inputImageHeight * size.height,
        );

        canvas.drawRect(rect, _paint);
      }
    }
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) {
    return true;
  }
}