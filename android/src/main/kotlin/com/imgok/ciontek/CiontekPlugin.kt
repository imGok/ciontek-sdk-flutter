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
        if (!handlePrinterStatus(result)) {
            return
        }

        CiontekPrintHelper.setupPrinter()

        when (call.method) {
            "print" -> handlePrint(call, result)
            "printBarcode" -> handlePrintBarcode(call, result)
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
        val lines = call.argument<List<Map<String, Any>>>("lines")
        if (lines.isNullOrEmpty()) {
            result.error("INVALID_ARGUMENT", "Text is required", null)
            return
        }

        val parsedLines = lines.map { PrintLine.fromMap(it) }
        parsedLines.forEach { CiontekPrintHelper.printLine(it) }
        result.success("Printing")
    }

    private fun handlePrintBarcode(call: MethodCall, result: Result) {
        val codeMap = call.argument<List<Map<String, Any>>>("codes")
        if (codeMap.isNullOrEmpty()) {
            result.error("INVALID_ARGUMENT", "Code is required", null)
            return
        }

        val parsedCodes = codeMap.map { PrintCode.fromMap(it) }
        parsedCodes.forEach { CiontekPrintHelper.printCode(it) }
        result.success("Printing")
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }
}