// nLevel：
// density level, value 1~5
// 1:Lowest 3：medium 5：Highest
enum TextGray { lowest, low, medium, high, highest }

enum CiontekPrintLineType {
  text,
  code128,
  code39,
  ean8,
  ean13,
  qrCode,
  pdf417,
  itf,
}

/// Print alignment mode
enum CiontekAlignMode {
  /// Left alignment
  left,

  /// Center alignment
  center,

  /// Right alignment
  right,
}

/// Base class for all printable items
abstract class CiontekPrintLine {
  const CiontekPrintLine();

  Map<String, dynamic> toMap();
}

/// Text line to print
class CiontekTextLine extends CiontekPrintLine {
  final String text;
  final TextGray textGray;
  final bool bold;
  final bool underline;
  final CiontekAlignMode alignMode;
  final bool invert;

  const CiontekTextLine({
    required this.text,
    this.textGray = TextGray.medium,
    this.bold = false,
    this.underline = false,
    this.alignMode = CiontekAlignMode.left,
    this.invert = false,
  });

  @override
  Map<String, dynamic> toMap() {
    return {
      'text': text,
      'textGray': textGray.index + 1,
      'bold': bold,
      'underline': underline,
      'alignMode': alignMode.name.toUpperCase(),
      'invert': invert,
      'type': 'TEXT',
    };
  }
}

/// QR Code line to print
class CiontekQRCodeLine extends CiontekPrintLine {
  final String data;
  final int size;
  final CiontekAlignMode alignMode;

  const CiontekQRCodeLine({
    required this.data,
    this.size = 256,
    this.alignMode = CiontekAlignMode.center,
  });

  @override
  Map<String, dynamic> toMap() {
    return {
      'text': data,
      'size': size,
      'alignMode': alignMode.name.toUpperCase(),
      'type': 'QR_CODE',
    };
  }
}

/// Barcode line to print
class CiontekBarcodeLine extends CiontekPrintLine {
  final String data;
  final CiontekPrintLineType barcodeType;
  final int width;
  final int height;
  final CiontekAlignMode alignMode;

  const CiontekBarcodeLine({
    required this.data,
    this.barcodeType = CiontekPrintLineType.code128,
    this.width = 380,
    this.height = 100,
    this.alignMode = CiontekAlignMode.center,
  }) : assert(
          barcodeType == CiontekPrintLineType.code128 ||
              barcodeType == CiontekPrintLineType.code39 ||
              barcodeType == CiontekPrintLineType.ean8 ||
              barcodeType == CiontekPrintLineType.ean13 ||
              barcodeType == CiontekPrintLineType.itf ||
              barcodeType == CiontekPrintLineType.pdf417,
          'Invalid barcode type',
        );

  @override
  Map<String, dynamic> toMap() {
    return {
      'text': data,
      'width': width,
      'height': height,
      'alignMode': alignMode.name.toUpperCase(),
      'type': switch (barcodeType) {
        CiontekPrintLineType.code128 => 'CODE_128',
        CiontekPrintLineType.code39 => 'CODE_39',
        CiontekPrintLineType.ean8 => 'EAN_8',
        CiontekPrintLineType.ean13 => 'EAN_13',
        CiontekPrintLineType.itf => 'ITF',
        CiontekPrintLineType.pdf417 => 'PDF_417',
        _ => 'CODE_128',
      },
    };
  }
}

/// Feed paper line
class CiontekFeedLine extends CiontekPrintLine {
  final int lines;

  const CiontekFeedLine({this.lines = 1});

  @override
  Map<String, dynamic> toMap() {
    return {
      'lines': lines,
      'type': 'FEED',
    };
  }
}
