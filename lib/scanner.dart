import 'dart:async';

import 'package:flutter/services.dart';

import 'models/scan_result.dart';

class CiontekScanner {
  const CiontekScanner();

  static const MethodChannel _method = MethodChannel('ciontek/scanner');
  static const EventChannel _events = EventChannel('ciontek/scanner/events');

  Future<void> start({int? timeout}) {
    return _method.invokeMethod('start', {
      if (timeout != null) 'timeout': timeout,
    });
  }

  Future<void> stop() {
    return _method.invokeMethod('stop');
  }

  Stream<ScanResult> get onScan => _events
      .receiveBroadcastStream()
      .map((e) => ScanResult.fromMap(e as Map<dynamic, dynamic>));
}
