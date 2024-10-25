
import 'package:ciontek/ciontek_print_line.dart';

import 'ciontek_platform_interface.dart';

class Ciontek {
  Future<String?> print({required List<CiontekPrintLine> lines}) {
    return CiontekPlatform.instance.print(lines);
  }
}
