import 'package:ciontek/models/ciontek_print_line.dart';
import 'package:flutter/material.dart';
import 'dart:async';

import 'package:ciontek/ciontek.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final _ciontekPlugin = Ciontek();

  @override
  void initState() {
    super.initState();
  }

  Future<void> printTest() async {
    await _ciontekPlugin.printLine(
      lines: [
        CiontekPrintLine(
          text:
              "Hello, this is a test print! Pho is the first thing you seek upon landing in Vietnam, always choosing vendors crowded with locals rather than tourists!",
          bold: true,
          textGray: TextGray.medium,
          underline: true,
        ),
        CiontekPrintLine(
          text: '------------------------------------------------',
          textGray: TextGray.medium,
        ),
        CiontekPrintLine(
          text: 'Good bye !',
          textGray: TextGray.highest,
        ),
        CiontekPrintLine.feedPaper(lines: 1),
        CiontekPrintLine(
          text: 'Welcome back !',
          underline: true,
        ),
        CiontekPrintLine(
          text: '12341234',
          type: CiontekPrintLineType.qrCode,
        ),
        CiontekPrintLine.feedPaper(lines: 4),
      ],
    );
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
            ],
          ),
        ),
      ),
    );
  }
}
