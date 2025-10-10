class ScanResult {
  final int length;
  final int encodeType; // 1=UTF-8, 2=GBK, 3=raw
  final int barType;
  final String? data;
  final List<int>? dataBytes;

  const ScanResult({
    required this.length,
    required this.encodeType,
    required this.barType,
    this.data,
    this.dataBytes,
  });

  factory ScanResult.fromMap(Map<dynamic, dynamic> map) {
    return ScanResult(
      length: map['length'] as int? ?? 0,
      encodeType: map['encodeType'] as int? ?? 1,
      barType: map['barType'] as int? ?? 0,
      data: map['data'] as String?,
      dataBytes: (map['dataBytes'] as List?)?.cast<int>(),
    );
  }
}
