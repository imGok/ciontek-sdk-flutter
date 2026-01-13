package com.imgok.ciontek

import com.ctk.sdk.PosApiHelper
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

class PrinterMethodHandler(
    private val posApiHelper: PosApiHelper,
) : MethodCallHandler {

    override fun onMethodCall(call: MethodCall, result: Result) {
        when (call.method) {
            "setFontPath" -> handleSetFontPath(call, result)
            "print" -> handlePrint(call, result)
            "getPrinterStatus" -> handleGetPrinterStatus(result)
            else -> result.notImplemented()
        }
    }

    private fun handleGetPrinterStatus(result: Result) {
        val status = posApiHelper.PrintCheckStatus()
        result.success(status)
    }

    private fun handlePrint(call: MethodCall, result: Result) {
        val status = posApiHelper.PrintCheckStatus()
        
        // Check printer status first
        when (status) {
            -1 -> {
                result.success(-1)  // No paper
                return
            }
            -2 -> {
                result.success(-2)  // Too hot
                return
            }
            -3 -> {
                result.success(-3)  // Low battery
                return
            }
        }

        // Printer is ready, proceed with printing
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
        result.success(0)  // Success
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
}
