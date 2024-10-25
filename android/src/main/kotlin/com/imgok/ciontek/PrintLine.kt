package com.imgok.ciontek

class PrintLine(
    val text: String,
    val textGray: Int,
    val bold: Boolean,
    val increaseFontSize: Boolean,
    val reverse: Boolean,
    val underline: Boolean
) {
    companion object {
        fun fromMap(map: Map<String, Any>): PrintLine {
            return PrintLine(
                text = map["text"] as String,
                textGray = map["textGray"] as Int,
                bold = map["bold"] as Boolean,
                increaseFontSize = map["increaseFontSize"] as Boolean,
                reverse = map["reverse"] as Boolean,
                underline = map["underline"] as Boolean
            )
        }
    }
}