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
            "print" -> {
                val lines = call.argument<List<Map<String, Any>>>("lines")
                if (!lines.isNullOrEmpty()) {
                    val checkStatus = posApiHelper.PrintCheckStatus()
                    if (checkStatus != 0) {
                        result.error("PRINT_ERROR", "Failed to check status", null)
                        return
                    }

                    val parsedLine = lines.map { line ->
                        PrintLine.fromMap(line)
                    }

                    for (line in parsedLine) {
                        CiontekLineHelper.print(line)
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