enum PrintResult {
  success,
  noPaper,
  tooHot,
  lowBattery,
  error;

  static PrintResult fromStatus(int status) {
    switch (status) {
      case 0:
        return PrintResult.success;
      case -1:
        return PrintResult.noPaper;
      case -2:
        return PrintResult.tooHot;
      case -3:
        return PrintResult.lowBattery;
      default:
        return PrintResult.error;
    }
  }

  bool get isSuccess => this == PrintResult.success;
  bool get hasError => this != PrintResult.success;
}
