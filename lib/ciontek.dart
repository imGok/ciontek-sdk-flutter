import 'ciontek_platform_interface.dart';
import 'models/ciontek_print_line.dart';

class Ciontek {
  Future<String?> printLine({required CiontekPrintLine line}) {
    return CiontekPlatform.instance.printLine(line);
  }

  Future<void> setFontPath(String path) {
    return CiontekPlatform.instance.setFontPath(path);
  }
}
