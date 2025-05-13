package com.imgok.ciontek

class PrintLine(
    val text: String,
    val textGray: Int,
    val bold: Boolean,
    val underline: Boolean,
    val type: String,
) {
    companion object {
        fun fromMap(map: Map<String, Any>): PrintLine {
            return PrintLine(
                text = map["text"] as String,
                textGray = map["textGray"] as Int,
                bold = map["bold"] as Boolean,
                underline = map["underline"] as Boolean,
                type = map["type"] as String,
            )
        }
    }
}