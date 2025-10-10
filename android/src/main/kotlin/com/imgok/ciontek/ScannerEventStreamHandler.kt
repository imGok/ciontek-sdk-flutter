package com.imgok.ciontek

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import io.flutter.plugin.common.EventChannel

class ScannerEventStreamHandler(private val context: Context) : EventChannel.StreamHandler {

    private var receiver: BroadcastReceiver? = null

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        if (events == null) return
        val filter = IntentFilter("ACTION_BAR_SCAN")
        receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                if (intent == null) return
                try {
                    val length = intent.getIntExtra("EXTRA_SCAN_LENGTH", 0)
                    val encodeType = intent.getIntExtra("EXTRA_SCAN_ENCODE_MODE", 1)
                    val barType = intent.getIntExtra("EXTRA_SCAN_BARTYPE", 0)
                    val payload: MutableMap<String, Any?> = HashMap()
                    payload["length"] = length
                    payload["encodeType"] = encodeType
                    payload["barType"] = barType
                    if (encodeType == 3) { // ENCODE_MODE_NONE -> raw bytes
                        val data = intent.getByteArrayExtra("EXTRA_SCAN_DATA")
                        payload["dataBytes"] = data
                    } else {
                        val data = intent.getStringExtra("EXTRA_SCAN_DATA")
                        payload["data"] = data
                    }
                    events.success(payload)
                } catch (t: Throwable) {
                    events.error("SCAN_ERROR", t.message, null)
                }
            }
        }
        context.registerReceiver(receiver, filter)
    }

    override fun onCancel(arguments: Any?) {
        try {
            receiver?.let { context.unregisterReceiver(it) }
        } catch (_: Throwable) {
        } finally {
            receiver = null
        }
    }
}
