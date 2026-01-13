import 'package:ciontek/models/ciontek_print_line.dart';
import 'package:ciontek/models/print_result.dart';
import 'package:ciontek/models/printer_status.dart';
import 'ciontek_platform_interface.dart';

class CiontekPrinter {
  const CiontekPrinter();

  /// Print multiple lines (text, QR codes, barcodes, etc.)
  Future<PrintResult> printLines(List<CiontekPrintLine> lines) {
    return CiontekPlatform.instance.printLines(lines);
  }

  /// Print a single line (deprecated, use printLines instead)
  @Deprecated('Use printLines instead')
  Future<PrintResult> printLine({required CiontekPrintLine line}) {
    return printLines([line]);
  }

  Future<void> setFontPath(String path) {
    return CiontekPlatform.instance.setFontPath(path);
  }

  Future<PrinterStatus> getPrinterStatus() {
    return CiontekPlatform.instance.getPrinterStatus();
  }
}
