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
        posApiHelper.PrintInit()
        
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
                // Set text properties
                posApiHelper.PrintSetBold(if (line.bold) 1 else 0)
                posApiHelper.PrintSetUnderline(if (line.underline) 1 else 0)
                posApiHelper.PrintSetReverse(if (line.invert) 1 else 0)
                val gray = line.textGray.coerceIn(1, 5)
                posApiHelper.PrintSetGray(gray)
                
                // Use PrintTableText for alignment support
                val alignment = when (line.alignMode?.uppercase()) {
                    "LEFT" -> 0
                    "CENTER" -> 1
                    "RIGHT" -> 2
                    else -> 0
                }
                
                // Create a single column table with the desired alignment
                posApiHelper.PrintTableText(
                    arrayOf(line.text),
                    intArrayOf(10),
                    intArrayOf(alignment)
                )
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
        
        for (line in lines) {
            printLine(line)
        }
        
        // Start the print job
        posApiHelper.PrintStart()
    }

    @Synchronized
    fun printText(text: String) {
        printLine(PrintLine(text, 3, false, false, "TEXT"))
    }
}