// nLevel：
// density level, value 1~5
// 1:Lowest 3：medium 5：Highest
enum TextGray { lowest, low, medium, high, highest }

enum CiontekPrintLineType {
  text,
  code128,
  code39,
  ean8,
  qrCode,
  pdf417,
  itf,
}

class CiontekPrintLine {
  final String text;
  final TextGray textGray;
  final bool bold;
  final bool underline;
  final CiontekPrintLineType type;

  CiontekPrintLine({
    required this.text,
    this.textGray = TextGray.medium,
    this.bold = false,
    this.underline = false,
    this.type = CiontekPrintLineType.text,
  });

  // Feed factory
  factory CiontekPrintLine.feedPaper({int lines = 1}) {
    return CiontekPrintLine(
      text: '\n ' * lines,
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'text': text,
      'textGray': textGray.index + 1,
      'bold': bold,
      'underline': underline,
      'type': switch (type) {
        CiontekPrintLineType.text => 'TEXT',
        CiontekPrintLineType.code128 => 'CODE_128',
        CiontekPrintLineType.code39 => 'CODE_39',
        CiontekPrintLineType.ean8 => 'EAN_8',
        CiontekPrintLineType.qrCode => 'QR_CODE',
        CiontekPrintLineType.pdf417 => 'PDF_417',
        CiontekPrintLineType.itf => 'ITF',
      },
    };
  }
}

extension PrintLineListExtension on List<CiontekPrintLine> {
  Map<String, dynamic> toMap() {
    return {
      "lines": map((e) => e.toMap()).toList(),
    };
  }
}
