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
        val checkStatus = posApiHelper.PrintCheckStatus()

        if (checkStatus == -1) {
            result.error("NO_PAPER_ERROR", "Error, No Paper", null)
            return
        }

        if (checkStatus == -2) {
            result.error("PRINTER_TOO_HOT", "Error, Printer Too Hot", null)
            return
        }

        if (checkStatus == -3) {
            result.error("LOW_BATTERY", "Error, Low Battery", null)
            return
        }

        when (call.method) {
            "print" -> {
                val lines = call.argument<List<Map<String, Any>>>("lines")
                if (!lines.isNullOrEmpty()) {
                    CiontekPrintHelper.setupPrinter()
                    val parsedLine = lines.map { line ->
                        PrintLine.fromMap(line)
                    }

                    for (line in parsedLine) {
                        CiontekPrintHelper.printLine(line)
                    }
                    result.success("Printing")
                } else {
                    result.error("INVALID_ARGUMENT", "Text is required", null)
                }
            }
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }
}