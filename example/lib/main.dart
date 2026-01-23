import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'dart:async';
import 'dart:io';
import 'package:path_provider/path_provider.dart';

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
  String? _lastPrintResult;

  @override
  void initState() {
    super.initState();
    // Temporairement désactivé pour tester avec police système
    // _setupPrinterFont();
    _scanSub = Ciontek.scanner.onScan.listen((e) {
      setState(() {
        _lastScan = e.data ??
            (e.dataBytes
                    ?.map((b) => b.toRadixString(16).padLeft(2, '0'))
                    .join(' ') ??
                '');
      });
    });
  }

  Future<void> _setupPrinterFont() async {
    try {
      // Load the font from assets
      final byteData =
          await rootBundle.load('assets/fonts/ciontek-printer-font.ttf');

      // Use the app's cache directory (no permissions needed)
      final directory = await getApplicationCacheDirectory();
      final fontPath = '${directory.path}/ciontek-printer-font.ttf';

      // Write the font file to the device
      final file = File(fontPath);
      await file.writeAsBytes(byteData.buffer.asUint8List());

      debugPrint('Font saved to: $fontPath (${file.lengthSync()} bytes)');

      // Set the font path in the plugin
      await Ciontek.printer.setFontPath(fontPath);
    } catch (e) {
      debugPrint('Error setting up printer font: $e');
    }
  }

  @override
  void dispose() {
    _scanSub?.cancel();
    super.dispose();
  }

  Future<void> printTest() async {
    try {
      setState(() => _lastPrintResult = 'Impression en cours...');

      final now = DateTime.now();
      final ticketNumber =
          'TKT${now.year}${now.month.toString().padLeft(2, '0')}${now.day.toString().padLeft(2, '0')}${now.hour.toString().padLeft(2, '0')}${now.minute.toString().padLeft(2, '0')}';

      final result = await Ciontek.printer.printLines([
        // En-tête du restaurant
        const CiontekTextLine(
          text: 'RESTAURANT LE BON GOUT',
          bold: true,
          textGray: TextGray.highest,
          alignMode: CiontekAlignMode.center,
        ),
        const CiontekTextLine(
          text: '123 Rue de la Paix',
          alignMode: CiontekAlignMode.center,
        ),
        const CiontekTextLine(
          text: '75001 Paris, France',
          alignMode: CiontekAlignMode.center,
        ),
        const CiontekTextLine(
          text: 'Tel: +33 1 23 45 67 89',
          alignMode: CiontekAlignMode.center,
        ),
        const CiontekFeedLine(lines: 1),
        const CiontekTextLine(
          text: '===========================',
          alignMode: CiontekAlignMode.center,
        ),
        const CiontekFeedLine(lines: 1),

        // Informations de la commande
        CiontekTextLine(
          text:
              'Date: ${now.day.toString().padLeft(2, '0')}/${now.month.toString().padLeft(2, '0')}/${now.year}',
        ),
        CiontekTextLine(
          text:
              'Heure: ${now.hour.toString().padLeft(2, '0')}:${now.minute.toString().padLeft(2, '0')}',
        ),
        CiontekTextLine(
          text: 'Ticket: $ticketNumber',
        ),
        const CiontekTextLine(
          text: 'Serveur: Marie D.',
        ),
        const CiontekTextLine(
          text: 'Table: 12',
        ),
        const CiontekFeedLine(lines: 1),
        const CiontekTextLine(
          text: '---------------------------',
          alignMode: CiontekAlignMode.center,
        ),
        const CiontekFeedLine(lines: 1),

        // Articles
        const CiontekTextLine(
          text: 'ARTICLES',
          bold: true,
          textGray: TextGray.high,
        ),
        const CiontekFeedLine(lines: 1),
        const CiontekTextLine(
          text: '2x Burger Classique',
        ),
        const CiontekTextLine(
          text: '19.80 EUR',
          alignMode: CiontekAlignMode.right,
        ),
        const CiontekTextLine(
          text: '1x Pizza Margherita',
        ),
        const CiontekTextLine(
          text: '15.50 EUR',
          alignMode: CiontekAlignMode.right,
        ),
        const CiontekTextLine(
          text: '2x Coca-Cola',
        ),
        const CiontekTextLine(
          text: '5.00 EUR',
          alignMode: CiontekAlignMode.right,
        ),
        const CiontekTextLine(
          text: '1x Tiramisu',
        ),
        const CiontekTextLine(
          text: '6.50 EUR',
          alignMode: CiontekAlignMode.right,
        ),
        const CiontekTextLine(
          text: '1x Cafe expresso',
        ),
        const CiontekTextLine(
          text: '2.50 EUR',
          alignMode: CiontekAlignMode.right,
        ),
        const CiontekFeedLine(lines: 1),
        const CiontekTextLine(
          text: '---------------------------',
          alignMode: CiontekAlignMode.center,
        ),
        const CiontekFeedLine(lines: 1),

        // Totaux
        const CiontekTextLine(
          text: 'Sous-total    49.30 EUR',
          alignMode: CiontekAlignMode.right,
        ),
        const CiontekTextLine(
          text: 'TVA (20%)      9.86 EUR',
          alignMode: CiontekAlignMode.right,
        ),
        const CiontekFeedLine(lines: 1),
        const CiontekTextLine(
          text: 'TOTAL         59.16 EUR',
          bold: true,
          textGray: TextGray.highest,
          alignMode: CiontekAlignMode.right,
        ),
        const CiontekFeedLine(lines: 1),
        const CiontekTextLine(
          text: '===========================',
          alignMode: CiontekAlignMode.center,
        ),
        const CiontekFeedLine(lines: 2),

        // Mode de paiement
        const CiontekTextLine(
          text: 'PAIEMENT',
          bold: true,
          alignMode: CiontekAlignMode.center,
        ),
        const CiontekTextLine(
          text: 'Carte Bancaire',
          alignMode: CiontekAlignMode.center,
        ),
        const CiontekFeedLine(lines: 2),

        // QR Code pour le feedback ou le menu
        const CiontekTextLine(
          text: 'Scannez pour un avis',
          alignMode: CiontekAlignMode.center,
        ),
        const CiontekFeedLine(lines: 1),
        CiontekQRCodeLine(
          data: 'https://restaurant.fr/$ticketNumber',
          size: 256,
          alignMode: CiontekAlignMode.center,
        ),
        const CiontekFeedLine(lines: 2),

        // Code-barres du ticket
        const CiontekTextLine(
          text: 'Numero de ticket',
          alignMode: CiontekAlignMode.center,
        ),
        const CiontekFeedLine(lines: 1),
        CiontekBarcodeLine(
          data: ticketNumber,
          barcodeType: CiontekPrintLineType.code128,
          width: 380,
          height: 100,
          alignMode: CiontekAlignMode.center,
        ),
        const CiontekFeedLine(lines: 2),

        // Message de fin
        const CiontekTextLine(
          text: 'Merci de votre visite!',
          bold: true,
          alignMode: CiontekAlignMode.center,
        ),
        const CiontekTextLine(
          text: 'A bientot!',
          alignMode: CiontekAlignMode.center,
        ),
        const CiontekFeedLine(lines: 1),
        const CiontekTextLine(
          text: 'TVA FR 12 345 678 901',
          alignMode: CiontekAlignMode.center,
        ),
        const CiontekTextLine(
          text: 'SIRET: 123 456 789 00012',
          alignMode: CiontekAlignMode.center,
        ),

        // Espace final
        const CiontekFeedLine(lines: 4),
      ]);

      setState(() => _lastPrintResult = 'Résultat: ${result.name}');
    } catch (e) {
      setState(() => _lastPrintResult = 'Erreur: $e');
    }
  }

  Future<void> printTestAlignment() async {
    try {
      setState(() => _lastPrintResult = 'Test alignement...');

      final result = await Ciontek.printer.printLines([
        const CiontekTextLine(
          text: 'TEST ALIGNEMENT',
          bold: true,
          alignMode: CiontekAlignMode.center,
        ),
        const CiontekTextLine(
          text: '===========================',
          alignMode: CiontekAlignMode.center,
        ),
        const CiontekFeedLine(lines: 1),

        // Test gauche
        const CiontekTextLine(
          text: 'Gauche (defaut)',
          bold: true,
        ),
        const CiontekTextLine(
          text: 'Court',
        ),
        const CiontekTextLine(
          text: 'Texte moyen test',
        ),
        const CiontekTextLine(
          text: 'Texte long pour test',
        ),
        const CiontekFeedLine(lines: 1),

        // Test centre
        const CiontekTextLine(
          text: 'Centre',
          bold: true,
          alignMode: CiontekAlignMode.center,
        ),
        const CiontekTextLine(
          text: 'Court',
          alignMode: CiontekAlignMode.center,
        ),
        const CiontekTextLine(
          text: 'Texte moyen test',
          alignMode: CiontekAlignMode.center,
        ),
        const CiontekTextLine(
          text: 'Texte long pour test',
          alignMode: CiontekAlignMode.center,
        ),
        const CiontekFeedLine(lines: 1),

        // Test droite
        const CiontekTextLine(
          text: 'Droite',
          bold: true,
          alignMode: CiontekAlignMode.right,
        ),
        const CiontekTextLine(
          text: 'Court',
          alignMode: CiontekAlignMode.right,
        ),
        const CiontekTextLine(
          text: 'Moyen test',
          alignMode: CiontekAlignMode.right,
        ),
        const CiontekTextLine(
          text: 'Long pour test',
          alignMode: CiontekAlignMode.right,
        ),
        const CiontekTextLine(
          text: '10.50 EUR',
          alignMode: CiontekAlignMode.right,
        ),
        const CiontekTextLine(
          text: '100.00 EUR',
          alignMode: CiontekAlignMode.right,
        ),
        const CiontekFeedLine(lines: 1),

        // Test séparateurs
        const CiontekTextLine(
          text: 'Separateurs',
          bold: true,
        ),
        const CiontekTextLine(
          text: '===========================',
        ),
        const CiontekTextLine(
          text: '---------------------------',
        ),

        const CiontekFeedLine(lines: 3),
      ]);

      setState(() => _lastPrintResult = 'Résultat: ${result.name}');
    } catch (e) {
      setState(() => _lastPrintResult = 'Erreur: $e');
    }
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
              const SizedBox(height: 12),
              OutlinedButton(
                style: OutlinedButton.styleFrom(
                  backgroundColor: Colors.blue,
                  side: BorderSide.none,
                ),
                onPressed: printTestAlignment,
                child: const Text(
                  'Test Alignment',
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
              if (_lastPrintResult != null)
                Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 16),
                  child: Text(
                    _lastPrintResult!,
                    textAlign: TextAlign.center,
                    style: TextStyle(
                      color: _lastPrintResult!.contains('Erreur')
                          ? Colors.red
                          : Colors.green,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
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
