import 'package:ciontek/models/ciontek_print_line.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'ciontek_platform_interface.dart';

/// An implementation of [CiontekPlatform] that uses method channels.
class MethodChannelCiontek extends CiontekPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('ciontek');

  @override
  Future<String?> printLine(List<CiontekPrintLine> lines) async {
    final result = await methodChannel.invokeMethod<String>(
      'print',
      lines.toMap(),
    );
    return result;
  }
}
