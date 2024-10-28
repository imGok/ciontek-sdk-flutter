A Flutter plugin to use the Ciontek built-in printer.
This plugin is a wrapper around the Ciontek SDK for Android.
Only works on Android devices, especially on Ciontek devices.

## Installation

```
flutter pub add ciontek_printer
```

## Tested Devices
```
Ciontek CS50C
```

## Exemple Code
```dart
await _ciontekPlugin.print(
  lines: [
    CiontekPrintLine(
      text: 'Hello, World!',
      bold: true,
      increaseFontSize: true,
      textGray: TextGray.medium,
      underline: true,
    ),
    CiontekPrintLine(
      text: 'Good bye !',
      reverse: true,
      textGray: TextGray.highest,
    ),
    CiontekPrintLine.feedPaper(lines: 1),
    CiontekPrintLine(
      text: 'Welcome back ! \n \n',
      underline: true,
    ),
  ],
);
```
