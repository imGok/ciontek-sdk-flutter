enum PrinterStatus {
  ready,
  noPaper,
  tooHot,
  lowBattery,
  unknown;

  static PrinterStatus fromValue(int value) {
    switch (value) {
      case 0:
        return PrinterStatus.ready;
      case -1:
        return PrinterStatus.noPaper;
      case -2:
        return PrinterStatus.tooHot;
      case -3:
        return PrinterStatus.lowBattery;
      default:
        return PrinterStatus.unknown;
    }
  }
}
