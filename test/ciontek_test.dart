import 'package:ciontek/ciontek_print_line.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:ciontek/ciontek_platform_interface.dart';
import 'package:ciontek/ciontek_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockCiontekPlatform
    with MockPlatformInterfaceMixin
    implements CiontekPlatform {
  @override
  Future<String?> print(List<CiontekPrintLine> lines) {
    throw UnimplementedError();
  }
}

void main() {
  final CiontekPlatform initialPlatform = CiontekPlatform.instance;

  test('$MethodChannelCiontek is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelCiontek>());
  });
}
