package com.imgok.ciontek

import android.content.Context
import android.content.Intent
import android.util.Log
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

class ScannerMethodHandler(private val context: Context) : MethodCallHandler {

    companion object {
        private const val TAG = "CiontekScanner"
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        when (call.method) {
            "start" -> handleStart(call, result)
            "stop" -> handleStop(result)
            else -> result.notImplemented()
        }
    }

    private fun handleStart(call: MethodCall, result: Result) {
        val timeoutArg = call.argument<Int>("timeout")
        val intent = Intent("ACTION_BAR_TRIGSCAN")
        if (timeoutArg != null) {
            val clamped = timeoutArg.coerceIn(1, 9)
            intent.putExtra("timeout", clamped)
            Log.d(TAG, "Sending broadcast ACTION_BAR_TRIGSCAN with timeout=$clamped")
        } else {
            Log.d(TAG, "Sending broadcast ACTION_BAR_TRIGSCAN with default timeout")
        }
        val sentTo = context.packageName
        Log.d(TAG, "Context package=$sentTo, intent=$intent")
        context.sendBroadcast(intent)
        Log.d(TAG, "Broadcast ACTION_BAR_TRIGSCAN sent")
        result.success(null)
    }

    private fun handleStop(result: Result) {
        val intent = Intent("ACTION_BAR_TRIGSTOP")
        Log.d(TAG, "Sending broadcast ACTION_BAR_TRIGSTOP, intent=$intent")
        context.sendBroadcast(intent)
        Log.d(TAG, "Broadcast ACTION_BAR_TRIGSTOP sent")
        result.success(null)
    }
}
