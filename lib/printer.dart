import 'package:ciontek/models/ciontek_print_line.dart';
import 'package:ciontek/models/print_result.dart';
import 'package:ciontek/models/printer_status.dart';
import 'ciontek_platform_interface.dart';

class CiontekPrinter {
  const CiontekPrinter();

  Future<PrintResult> printLine({required CiontekPrintLine line}) {
    return CiontekPlatform.instance.printLine(line);
  }

  Future<void> setFontPath(String path) {
    return CiontekPlatform.instance.setFontPath(path);
  }

  Future<PrinterStatus> getPrinterStatus() {
    return CiontekPlatform.instance.getPrinterStatus();
  }
}
