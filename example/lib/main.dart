import 'package:flutter/material.dart';
import 'dart:async';

import 'package:ciontek/ciontek.dart';
import 'package:ciontek/models/ciontek_print_line.dart';
import 'package:ciontek/models/scan_result.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  // Scan state
  StreamSubscription<ScanResult>? _scanSub;
  String? _lastScan;
  int _timeoutSeconds = 4;

  @override
  void initState() {
    super.initState();
    _scanSub = Ciontek.scanner.onScan.listen((e) {
      setState(() {
        _lastScan = e.data ?? (e.dataBytes?.map((b) => b.toRadixString(16).padLeft(2, '0')).join(' ') ?? '');
      });
    });
  }

  @override
  void dispose() {
    _scanSub?.cancel();
    super.dispose();
  }

  Future<void> printTest() async {
  await Ciontek.printer.printLine(
      line: CiontekPrintLine(
        text:
            "Hello, this is a test print! Pho is the first thing you seek upon landing in Vietnam, always choosing vendors crowded with locals rather than tourists!",
        bold: true,
        textGray: TextGray.medium,
        underline: true,
      ),
    );
  await Ciontek.printer.printLine(line: CiontekPrintLine.feedPaper(lines: 5));
  }

  Future<void> startScan() async {
    await Ciontek.scanner.start(timeout: _timeoutSeconds);
  }

  Future<void> stopScan() async {
    await Ciontek.scanner.stop();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Ciontek Print Example'),
        ),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisSize: MainAxisSize.max,
            children: [
              OutlinedButton(
                style: OutlinedButton.styleFrom(
                  backgroundColor: Colors.purple,
                  side: BorderSide.none,
                ),
                onPressed: printTest,
                child: const Text(
                  'Print something!',
                  style: TextStyle(
                    fontSize: 20,
                    color: Colors.white,
                  ),
                ),
              ),
              const SizedBox(height: 24),
              // Scan controls
              Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  SizedBox(
                    width: 120,
                    child: TextFormField(
                      initialValue: _timeoutSeconds.toString(),
                      keyboardType: TextInputType.number,
                      decoration: const InputDecoration(
                        labelText: 'Timeout (1-9s)',
                        border: OutlineInputBorder(),
                        isDense: true,
                      ),
                      onChanged: (v) {
                        final n = int.tryParse(v) ?? _timeoutSeconds;
                        setState(() {
                          _timeoutSeconds = n.clamp(1, 9);
                        });
                      },
                    ),
                  ),
                  const SizedBox(width: 12),
                  OutlinedButton(
                    onPressed: startScan,
                    child: const Text('Start scan'),
                  ),
                  const SizedBox(width: 8),
                  OutlinedButton(
                    onPressed: stopScan,
                    child: const Text('Stop scan'),
                  ),
                ],
              ),
              const SizedBox(height: 12),
              if (_lastScan != null)
                Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 16),
                  child: Text(
                    'Last scan:\n$_lastScan',
                    textAlign: TextAlign.center,
                  ),
                ),
            ],
          ),
        ),
      ),
    );
  }
}
