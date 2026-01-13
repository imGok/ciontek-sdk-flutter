package com.imgok.ciontek

class PrintLine(
    val text: String,
    val textGray: Int,
    val bold: Boolean,
    val underline: Boolean,
    val type: String,
    val alignMode: String? = null,
    val invert: Boolean = false,
    val size: Int? = null,
    val width: Int? = null,
    val height: Int? = null,
    val lines: Int? = null,
) {
    companion object {
        fun fromMap(map: Map<String, Any>): PrintLine {
            return PrintLine(
                text = map["text"] as? String ?: "",
                textGray = map["textGray"] as? Int ?: 3,
                bold = map["bold"] as? Boolean ?: false,
                underline = map["underline"] as? Boolean ?: false,
                type = map["type"] as String,
                alignMode = map["alignMode"] as? String,
                invert = map["invert"] as? Boolean ?: false,
                size = map["size"] as? Int,
                width = map["width"] as? Int,
                height = map["height"] as? Int,
                lines = map["lines"] as? Int,
            )
        }
    }
}