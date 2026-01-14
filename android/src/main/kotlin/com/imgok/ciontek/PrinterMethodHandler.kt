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
            "printLines" -> handlePrintLines(call, result)
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
        val maybeMap = call.arguments as? Map<*, *>
        if (maybeMap == null) {
            result.error("INVALID_ARGUMENT", "Line map is required", null)
            return
        }
        @Suppress("UNCHECKED_CAST")
        val map = maybeMap as Map<String, Any>
        val line = PrintLine.fromMap(map)
        // Use printLines which handles setup and PrintStart correctly
        CiontekPrintHelper.printLines(listOf(line))
        result.success(0)  // Success
    }

    private fun handlePrintLines(call: MethodCall, result: Result) {
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
        val arguments = call.arguments as? Map<*, *>
        if (arguments == null) {
            result.error("INVALID_ARGUMENT", "Arguments map is required", null)
            return
        }
        
        val linesData = arguments["lines"] as? List<*>
        if (linesData == null) {
            result.error("INVALID_ARGUMENT", "Lines list is required", null)
            return
        }

        try {
            val lines = linesData.mapNotNull { lineData ->
                @Suppress("UNCHECKED_CAST")
                (lineData as? Map<String, Any>)?.let { PrintLine.fromMap(it) }
            }
            
            if (lines.isEmpty()) {
                result.error("INVALID_ARGUMENT", "No valid lines to print", null)
                return
            }

            // Print the lines (setupPrinter is called inside printLines)
            CiontekPrintHelper.printLines(lines)
            result.success(0)  // Success
        } catch (e: Exception) {
            result.error("PRINT_ERROR", "Error printing lines: ${e.message}", null)
        }
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
