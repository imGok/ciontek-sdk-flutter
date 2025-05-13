package com.imgok.ciontek

class PrintCode(
    val data: String,
    val barcodeType: String,
    val width: Int,
    val height: Int
) {
    companion object {
        fun fromMap(map: Map<String, Any>): PrintCode {
            return PrintCode(
                data = map["data"] as String,
                barcodeType = map["barcodeType"] as String,
                width = map["width"] as Int,
                height = map["height"] as Int
            )
        }
    }
}