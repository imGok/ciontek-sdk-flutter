package com.imgok.ciontek

import com.ctk.sdk.PosApiHelper
import java.io.File

object CiontekPrintHelper {
    private val posApiHelper: PosApiHelper = PosApiHelper.getInstance()

    @Volatile
    private var fontPath: String? = null

    @Volatile
    private var initialized: Boolean = false

    private const val ALIGN_LEFT = 0
    private const val ALIGN_CENTER = 1
    private const val ALIGN_RIGHT = 2
    private const val DEFAULT_BARCODE_WIDTH = 360
    private const val DEFAULT_BARCODE_HEIGHT = 120
    private const val DEFAULT_QR_SIZE = 256
    private const val LINE_WIDTH = 28  // Width for 58mm thermal printer with 24x24 font

    @Synchronized
    fun setFontPath(path: String) {
        fontPath = path
        if (initialized) {
            // Re-apply the font if printer already initialized
            if (File(path).exists()) {
                posApiHelper.PrintSetFontTTF(path, 24.toByte(), 24.toByte())
            }
        }
    }

    @Synchronized
    fun setupPrinter() {
        // Always initialize before a print job
        // PrintInit(type, fontWidth, fontHeight, flags)
        posApiHelper.PrintInit(2, 24, 24, 0x33)
        
        // Set font size
        posApiHelper.PrintSetFont(24.toByte(), 24.toByte(), 24.toByte())
        
        // Only set custom font if path is provided and file exists
        if (fontPath != null && File(fontPath!!).exists()) {
            posApiHelper.PrintSetFontTTF(fontPath!!, 24.toByte(), 24.toByte())
        } else {
            // Try to use system monospace font if available
            val monospaceFonts = listOf(
                "/system/fonts/DroidSansMono.ttf",
                "/system/fonts/RobotoMono-Regular.ttf",
                "/system/fonts/CutiveMono.ttf"
            )
            
            val availableFont = monospaceFonts.firstOrNull { File(it).exists() }
            if (availableFont != null) {
                posApiHelper.PrintSetFontTTF(availableFont, 24.toByte(), 24.toByte())
            }
        }
        
        initialized = true
    }

    /**
     * Format text with manual alignment by adding spaces
     */
    private fun formatTextWithAlignment(text: String, alignment: Int): String {
        val textLength = text.length
        
        return when (alignment) {
            ALIGN_LEFT -> {
                // Left alignment: just return the text
                text
            }
            ALIGN_CENTER -> {
                // Center alignment: add spaces on both sides
                val spacesNeeded = (LINE_WIDTH - textLength) / 2
                if (spacesNeeded > 0) {
                    " ".repeat(spacesNeeded) + text
                } else {
                    text
                }
            }
            ALIGN_RIGHT -> {
                // Right alignment: add spaces on the left
                val spacesNeeded = LINE_WIDTH - textLength
                if (spacesNeeded > 0) {
                    " ".repeat(spacesNeeded) + text
                } else {
                    text
                }
            }
            else -> text
        }
    }

    @Synchronized
    fun setLineSettings(line: PrintLine) {
        // Set alignment
        val alignment = when (line.alignMode?.uppercase()) {
            "LEFT" -> ALIGN_LEFT
            "CENTER" -> ALIGN_CENTER
            "RIGHT" -> ALIGN_RIGHT
            else -> ALIGN_LEFT
        }
        posApiHelper.PrintSetAlign(alignment)

        // Set text properties
        posApiHelper.PrintSetBold(if (line.bold) 1 else 0)
        posApiHelper.PrintSetUnderline(if (line.underline) 1 else 0)
        posApiHelper.PrintSetReverse(if (line.invert) 1 else 0)
        
        val gray = line.textGray.coerceIn(1, 5)
        posApiHelper.PrintSetGray(gray)
    }

    @Synchronized
    fun printLine(line: PrintLine) {
        when (line.type) {
            "TEXT" -> {
                // Determine alignment
                val alignment = when (line.alignMode?.uppercase()) {
                    "LEFT" -> ALIGN_LEFT
                    "CENTER" -> ALIGN_CENTER
                    "RIGHT" -> ALIGN_RIGHT
                    else -> ALIGN_LEFT
                }
                
                // Set text properties
                posApiHelper.PrintSetBold(if (line.bold) 1 else 0)
                posApiHelper.PrintSetUnderline(if (line.underline) 1 else 0)
                posApiHelper.PrintSetReverse(if (line.invert) 1 else 0)
                val gray = line.textGray.coerceIn(1, 5)
                posApiHelper.PrintSetGray(gray)
                
                // Format text with manual alignment by adding spaces
                val formattedText = formatTextWithAlignment(line.text, alignment)
                
                // Print the formatted text
                posApiHelper.PrintStr(formattedText + "\n")
            }
            "QR_CODE" -> {
                val size = line.size ?: DEFAULT_QR_SIZE
                val alignment = when (line.alignMode?.uppercase()) {
                    "LEFT" -> ALIGN_LEFT
                    "CENTER" -> ALIGN_CENTER
                    "RIGHT" -> ALIGN_RIGHT
                    else -> ALIGN_CENTER
                }
                posApiHelper.PrintSetAlign(alignment)
                // QR codes are printed as barcodes with type "QR_CODE"
                posApiHelper.PrintBarcode(line.text, size, size, "QR_CODE")
                // Reset alignment to left after printing
                posApiHelper.PrintSetAlign(ALIGN_LEFT)
            }
            "FEED" -> {
                val feedLines = line.lines ?: 1
                repeat(feedLines) {
                    posApiHelper.PrintStr("\n")
                }
            }
            else -> {
                // Barcode printing
                val width = line.width ?: DEFAULT_BARCODE_WIDTH
                val height = line.height ?: DEFAULT_BARCODE_HEIGHT
                val alignment = when (line.alignMode?.uppercase()) {
                    "LEFT" -> ALIGN_LEFT
                    "CENTER" -> ALIGN_CENTER
                    "RIGHT" -> ALIGN_RIGHT
                    else -> ALIGN_CENTER
                }
                posApiHelper.PrintSetAlign(alignment)
                posApiHelper.PrintBarcode(line.text, width, height, line.type)
                // Reset alignment to left after printing
                posApiHelper.PrintSetAlign(ALIGN_LEFT)
            }
        }
    }

    @Synchronized
    fun printLines(lines: List<PrintLine>) {
        // Initialize printer for this print job
        setupPrinter()
        
        // Print all lines
        for (line in lines) {
            printLine(line)
        }
        
        // Start the print job - this actually sends the data to the printer
        posApiHelper.PrintStart()
    }

    @Synchronized
    fun printText(text: String) {
        printLine(PrintLine(text, 3, false, false, "TEXT"))
    }
}