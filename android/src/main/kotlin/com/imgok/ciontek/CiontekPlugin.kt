package com.imgok.ciontek

import com.ctk.sdk.PosApiHelper
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** CiontekPlugin */
class CiontekPlugin : FlutterPlugin, MethodCallHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private lateinit var posApiHelper: PosApiHelper

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "ciontek")
        channel.setMethodCallHandler(this)
        posApiHelper = PosApiHelper.getInstance()
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        when (call.method) {
            "setFontPath" -> handleSetFontPath(call, result)
            "print" -> handlePrint(call, result)
            else -> result.notImplemented()
        }
    }

    private fun handlePrinterStatus(result: Result): Boolean {
        return when (posApiHelper.PrintCheckStatus()) {
            -1 -> {
                result.error("NO_PAPER_ERROR", "Error, No Paper", null)
                false
            }
            -2 -> {
                result.error("PRINTER_TOO_HOT", "Error, Printer Too Hot", null)
                false
            }
            -3 -> {
                result.error("LOW_BATTERY", "Error, Low Battery", null)
                false
            }
            else -> true
        }
    }

    private fun handlePrint(call: MethodCall, result: Result) {
        if (!handlePrinterStatus(result)) {
            return
        }

        CiontekPrintHelper.setupPrinter()
        val maybeMap = call.arguments as? Map<*, *>
        if (maybeMap == null) {
            result.error("INVALID_ARGUMENT", "Line map is required", null)
            return
        }
        @Suppress("UNCHECKED_CAST")
        val map = maybeMap as Map<String, Any>
        val line = PrintLine.fromMap(map)
        CiontekPrintHelper.printLine(line)
        result.success("Printing")
    }

    private fun handleSetFontPath(call: MethodCall, result: Result) {
        val path = call.argument<String>("path")
        if (path.isNullOrBlank()) {
            result.error("INVALID_ARGUMENT", "path is required", null)
            return
        }
        CiontekPrintHelper.setFontPath(path)
        result.success(null)
    }

    // step method removed

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }
}