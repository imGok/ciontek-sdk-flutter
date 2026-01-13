import 'package:ciontek/models/ciontek_print_line.dart';
import 'package:ciontek/models/print_result.dart';
import 'package:ciontek/models/printer_status.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'ciontek_platform_interface.dart';

/// An implementation of [CiontekPlatform] that uses method channels.
class MethodChannelCiontek extends CiontekPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('ciontek/printer');

  @override
  Future<PrintResult> printLine(CiontekPrintLine line) async {
    return printLines([line]);
  }

  @override
  Future<PrintResult> printLines(List<CiontekPrintLine> lines) async {
    try {
      final linesData = lines.map((e) => e.toMap()).toList();
      final result = await methodChannel.invokeMethod<int>(
        'printLines',
        {'lines': linesData},
      );
      return PrintResult.fromStatus(result ?? 0);
    } on PlatformException catch (e) {
      // Handle errors from the native side
      switch (e.code) {
        case 'NO_PAPER_ERROR':
          return PrintResult.noPaper;
        case 'PRINTER_TOO_HOT':
          return PrintResult.tooHot;
        case 'LOW_BATTERY':
          return PrintResult.lowBattery;
        default:
          return PrintResult.error;
      }
    }
  }

  @override
  Future<void> setFontPath(String path) async {
    await methodChannel.invokeMethod<void>(
      'setFontPath',
      {
        'path': path,
      },
    );
  }

  @override
  Future<PrinterStatus> getPrinterStatus() async {
    final result = await methodChannel.invokeMethod<int>('getPrinterStatus');
    return PrinterStatus.fromValue(result ?? -999);
  }
}
