package com.imgok.ciontek

import com.ctk.sdk.PosApiHelper

object CiontekPrintHelper {
    private val posApiHelper: PosApiHelper = PosApiHelper.getInstance()

    private const val FONT_PATH = "/storage/emulated/0/Download/ciontek-printer-font.ttf"

    @Synchronized
    fun setupPrinter() {
        posApiHelper.PrintInit()
        posApiHelper.PrintSetFontTTF(FONT_PATH, 24.toByte(), 24.toByte());
    }

    @Synchronized
    fun setLineSettings(line: PrintLine) {
        posApiHelper.PrintSetBold(if (line.bold) 1 else 0)
        posApiHelper.PrintSetUnderline(if (line.underline) 1 else 0)
        posApiHelper.PrintSetGray(line.textGray)
        posApiHelper.PrintSetAlign(1)
    }

    @Synchronized
    fun printCode(code: PrintCode) {
        posApiHelper.PrintSetAlign(1)
        posApiHelper.PrintSetGray(0)
        posApiHelper.PrintBarcode(code.data, code.width, code.height, code.barcodeType)
        posApiHelper.PrintStart()
    }

    @Synchronized
    fun printLine(line: PrintLine) {
        setLineSettings(line)
        posApiHelper.PrintStr(line.text)
        posApiHelper.PrintStart()
    }
}