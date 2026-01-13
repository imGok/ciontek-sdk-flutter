import 'package:flutter_test/flutter_test.dart';
import 'package:ciontek/ciontek_platform_interface.dart';
import 'package:ciontek/ciontek_method_channel.dart';
import 'package:ciontek/models/print_result.dart';
import 'package:ciontek/models/printer_status.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';
import 'package:ciontek/models/ciontek_print_line.dart';

class MockCiontekPlatform
    with MockPlatformInterfaceMixin
    implements CiontekPlatform {
  @override
  Future<PrintResult> printLine(CiontekPrintLine line) async {
    return PrintResult.success;
  }

  @override
  Future<PrintResult> printLines(List<CiontekPrintLine> lines) async {
    return PrintResult.success;
  }

  @override
  Future<void> setFontPath(String path) async {
    // no-op for tests
  }

  @override
  Future<PrinterStatus> getPrinterStatus() async {
    return PrinterStatus.ready;
  }
}

void main() {
  final CiontekPlatform initialPlatform = CiontekPlatform.instance;

  test('$MethodChannelCiontek is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelCiontek>());
  });
}
