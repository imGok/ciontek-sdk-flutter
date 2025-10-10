package com.imgok.ciontek

import com.ctk.sdk.PosApiHelper
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** CiontekPlugin */
class CiontekPlugin : FlutterPlugin, MethodCallHandler {
    // Main plugin now delegates to dedicated feature channels (printer, scanner, etc.)
    private lateinit var printerChannel: MethodChannel
    private lateinit var printerHandler: PrinterMethodHandler
    private lateinit var scannerChannel: MethodChannel
    private lateinit var scannerHandler: ScannerMethodHandler
    private lateinit var scanEventsChannel: io.flutter.plugin.common.EventChannel
    private lateinit var scanEventsHandler: ScannerEventStreamHandler

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        // Setup printer sub-channel
        printerChannel = MethodChannel(flutterPluginBinding.binaryMessenger, "ciontek/printer")
        printerHandler = PrinterMethodHandler(PosApiHelper.getInstance())
        printerChannel.setMethodCallHandler(printerHandler)

    // Setup scanner sub-channels
    scannerChannel = MethodChannel(flutterPluginBinding.binaryMessenger, "ciontek/scanner")
    scannerHandler = ScannerMethodHandler(flutterPluginBinding.applicationContext)
    scannerChannel.setMethodCallHandler(scannerHandler)

    scanEventsChannel = io.flutter.plugin.common.EventChannel(flutterPluginBinding.binaryMessenger, "ciontek/scanner/events")
    scanEventsHandler = ScannerEventStreamHandler(flutterPluginBinding.applicationContext)
    scanEventsChannel.setStreamHandler(scanEventsHandler)
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        // Root channel currently unused; all calls should go to feature sub-channels.
        result.notImplemented()
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        printerChannel.setMethodCallHandler(null)
    scannerChannel.setMethodCallHandler(null)
    scanEventsChannel.setStreamHandler(null)
    }
}