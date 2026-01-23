package com.imgok.ciontek

import android.util.Log
import com.ctk.sdk.PosApiHelper
import java.io.File

object CiontekPrintHelper {
    private const val TAG = "CiontekPrintHelper"
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
    private const val PRINTER_WIDTH = 32  // Width in characters for this printer
    private const val LINE_WIDTH_NORMAL = 27  // Width for normal text (adjusted for 1 char overflow)
    private const val LINE_WIDTH_BOLD = 25    // Width for bold text (adjusted for 3 char overflow)

    @Synchronized
    fun setFontPath(path: String) {
        fontPath = path
        Log.d(TAG, "setFontPath: Received path = $path")
        
        val file = File(path)
        Log.d(TAG, "setFontPath: File exists = ${file.exists()}")
        if (file.exists()) {
            Log.d(TAG, "setFontPath: File size = ${file.length()} bytes")
            Log.d(TAG, "setFontPath: File readable = ${file.canRead()}")
        }
        
        if (initialized) {
            // Re-apply the font if printer already initialized
            if (file.exists()) {
                posApiHelper.PrintSetFontTTF(path, 24.toByte(), 24.toByte())
                Log.d(TAG, "setFontPath: Font applied to initialized printer")
            } else {
                Log.w(TAG, "setFontPath: Font file does not exist at $path")
            }
        } else {
            Log.d(TAG, "setFontPath: Printer not yet initialized, will apply on next print")
        }
    }

    @Synchronized
    fun setupPrinter() {
        Log.d(TAG, "setupPrinter: Starting printer initialization")
        // Always initialize before a print job
        // PrintInit(type, fontWidth, fontHeight, flags)
        posApiHelper.PrintInit(2, 24, 24, 0x33)
        
        // Set font size
        posApiHelper.PrintSetFont(24.toByte(), 24.toByte(), 24.toByte())
        Log.d(TAG, "setupPrinter: Default font size set")
        
        // Only set custom font if path is provided and file exists
        if (fontPath != null) {
            val file = File(fontPath!!)
            Log.d(TAG, "setupPrinter: Checking custom font path = $fontPath")
            Log.d(TAG, "setupPrinter: Custom font exists = ${file.exists()}")
            
            if (file.exists()) {
                posApiHelper.PrintSetFontTTF(fontPath!!, 24.toByte(), 24.toByte())
                Log.d(TAG, "setupPrinter: Custom font applied successfully")
                initialized = true
                return
            }
        } else {
            Log.d(TAG, "setupPrinter: No custom font path set")
        }
        
        // Try to use system monospace font if available
        Log.d(TAG, "setupPrinter: Looking for system monospace fonts")
        val monospaceFonts = listOf(
            "/system/fonts/DroidSansMono.ttf",
            "/system/fonts/RobotoMono-Regular.ttf",
            "/system/fonts/CutiveMono.ttf"
        )
        
        val availableFont = monospaceFonts.firstOrNull { 
            val exists = File(it).exists()
            Log.d(TAG, "setupPrinter: Checking $it = $exists")
            exists
        }
        
        if (availableFont != null) {
            posApiHelper.PrintSetFontTTF(availableFont, 24.toByte(), 24.toByte())
            Log.d(TAG, "setupPrinter: System monospace font applied: $availableFont")
        } else {
            Log.d(TAG, "setupPrinter: No monospace font found, using default")
        }
        
        initialized = true
    }

    /**
     * Format text with manual alignment by adding spaces
     */
    private fun formatTextWithAlignment(text: String, alignment: Int, isBold: Boolean): String {
        val textLength = text.length
        val lineWidth = if (isBold) LINE_WIDTH_BOLD else LINE_WIDTH_NORMAL
        
        return when (alignment) {
            ALIGN_LEFT -> {
                // Left alignment: just return the text
                Log.d(TAG, "Left align: '$text' (${textLength} chars, bold=$isBold)")
                text
            }
            ALIGN_CENTER -> {
                // Center alignment: add spaces on both sides
                val spacesNeeded = (lineWidth - textLength) / 2
                val result = if (spacesNeeded > 0) {
                    " ".repeat(spacesNeeded) + text
                } else {
                    text
                }
                Log.d(TAG, "Center align: '$text' (${textLength} chars, bold=$isBold) -> padding=$spacesNeeded, lineWidth=$lineWidth")
                result
            }
            ALIGN_RIGHT -> {
                // Right alignment: add spaces on the left
                val spacesNeeded = lineWidth - textLength
                val result = if (spacesNeeded > 0) {
                    " ".repeat(spacesNeeded) + text
                } else {
                    text
                }
                Log.d(TAG, "Right align: '$text' (${textLength} chars, bold=$isBold) -> padding=$spacesNeeded, lineWidth=$lineWidth, total=${result.length}")
                result
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
                // Pass bold flag to adjust line width accordingly
                val formattedText = formatTextWithAlignment(line.text, alignment, line.bold)
                
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