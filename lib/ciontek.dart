import 'printer.dart';
import 'scanner.dart';

export 'models/ciontek_print_line.dart';
export 'models/print_result.dart';
export 'models/printer_status.dart';

class Ciontek {
  Ciontek._();
  static const printer = CiontekPrinter();
  static const scanner = CiontekScanner();
}
