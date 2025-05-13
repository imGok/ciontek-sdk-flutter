// ignore_for_file: constant_identifier_names

enum CiontekPrintCodeType {
  code128(ciontekValue: "CODE_128"),
  code39(ciontekValue: "CODE_39"),
  ean8(ciontekValue: "EAN_8"),
  qrCode(ciontekValue: "QR_CODE"),
  pdf417(ciontekValue: "PDF_417"),
  itf(ciontekValue: "ITF"),
  ;

  const CiontekPrintCodeType({
    required this.ciontekValue,
  });

  final String ciontekValue;
}

class CiontekPrintCode {
  final String data;
  final CiontekPrintCodeType barcodeType;
  final int width;
  final int height;

  CiontekPrintCode({
    required this.data,
    this.barcodeType = CiontekPrintCodeType.ean8,
    this.width = 360,
    this.height = 120,
  });

  Map<String, dynamic> toMap() {
    return {
      'data': data,
      'barcodeType': barcodeType.ciontekValue,
      'width': width,
      'height': height,
    };
  }
}

extension PrintCodeListExtension on List<CiontekPrintCode> {
  Map<String, dynamic> toMap() {
    return {
      "codes": map((e) => e.toMap()).toList(),
    };
  }
}
