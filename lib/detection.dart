import 'dart:ui';

// {inferenceTime: 56, inputImageHeight: 640, inputImageWidth: 480, results: [{detections: [{boundingBox: {bottom: 475.0, left: 0.0, right: 451.0, top: 84.0}, categories: [{categoryName: VehicleType.bus, displayName: , index: -1, score: 0.9771044}], keypoints: {}}], timestampMs: 73382061}]}

class Detection {
  List<Category> categories;
  Rect boundingBox;
  List<Offset>? keyPoints;

  Detection({
    required this.categories,
    required this.boundingBox,
    required this.keyPoints,
  });

  factory Detection.fromMap(Map<String, dynamic> map) {
    final boundingBox = map['boundingBox'];
    final categories = map['categories'];

    final categoriesList = List<Category>.from(
        categories.map((x) => Category.fromMap(x)));

    final boundingBoxRect = Rect.fromLTRB(
      boundingBox['left'],
      boundingBox['top'],
      boundingBox['right'],
      boundingBox['bottom'],
    );

    return Detection(
      categories: categoriesList,
      boundingBox: boundingBoxRect,
      keyPoints: null,
    );
  }
}

class ObjectDetectorResult {
  int timestampMs;
  List<Detection> detections;

  ObjectDetectorResult({
    required this.timestampMs,
    required this.detections,
  });

  ObjectDetectorResult.fromMap(Map<String, dynamic> map)
      : timestampMs = map['timestampMs'],
        detections = List<Detection>.from(
            map['detections'].map((x) => Detection.fromMap(x)));
}

class ResultBundle {
  List<ObjectDetectorResult> results;
  int inferenceTime;
  int inputImageHeight;
  int inputImageWidth;

  ResultBundle({
    required this.results,
    required this.inferenceTime,
    required this.inputImageHeight,
    required this.inputImageWidth,
  });

  ResultBundle.fromMap(Map<String, dynamic> map)
      : results = List<ObjectDetectorResult>.from(
            map['results'].map((x) => ObjectDetectorResult.fromMap(x))),
        inferenceTime = map['inferenceTime'],
        inputImageHeight = map['inputImageHeight'],
        inputImageWidth = map['inputImageWidth'];

  @override
  String toString() {
    return 'ResultBundle{results: $results, inferenceTime: $inferenceTime, inputImageHeight: $inputImageHeight, inputImageWidth: $inputImageWidth}';
  }
}

class Category {
  String? displayName;
  String categoryName;
  double score;
  int index;

  Category({
    required this.displayName,
    required this.categoryName,
    required this.score,
    required this.index,
  });

  Category.fromMap(Map<String, dynamic> map)
      : displayName = map['displayName'],
        categoryName = map['categoryName'],
        score = map['score'],
        index = map['index'];
}
