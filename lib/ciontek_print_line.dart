// nLevel：
// density level, value 1~5
// 1:Lowest 3：medium 5：Highest
enum TextGray { lowest, low, medium, high, highest }

class CiontekPrintLine {
  final String text;
  final TextGray textGray;
  final bool bold;
  final bool underline;

  CiontekPrintLine({
    required this.text,
    this.textGray = TextGray.medium,
    this.bold = false,
    this.underline = false,
  });

  // Feed factory
  factory CiontekPrintLine.feedPaper({int lines = 1}) {
    return CiontekPrintLine(
      text: '\n' * lines,
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'text': text,
      'textGray': textGray.index + 1,
      'bold': bold,
      'underline': underline,
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
