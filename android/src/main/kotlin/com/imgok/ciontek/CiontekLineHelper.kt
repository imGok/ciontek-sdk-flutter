package com.imgok.ciontek

import com.ctk.sdk.PosApiHelper

object CiontekLineHelper {
    private val posApiHelper: PosApiHelper = PosApiHelper.getInstance()

    @Synchronized
    fun initLine(line: PrintLine) {
        posApiHelper.PrintSetBold(if (line.bold) 1 else 0)
        posApiHelper.PrintSetReverse(if (line.reverse) 1 else 0)
        if (line.increaseFontSize) {
            posApiHelper.PrintSetFont(24.toByte(), 24.toByte(), 0x00.toByte())
        }

        posApiHelper.PrintSetUnderline(if (line.underline) 1 else 0)
        posApiHelper.PrintSetGray(line.textGray)
    }

    @Synchronized
    fun resetLine() {
        posApiHelper.PrintSetBold(0)
        posApiHelper.PrintSetReverse(0)
        posApiHelper.PrintSetFont(16.toByte(), 16.toByte(), 0x33.toByte())
        posApiHelper.PrintSetGray(3)
        posApiHelper.PrintSetUnderline(0)
    }

    @Synchronized
    fun print(line: PrintLine) {
        initLine(line)
        posApiHelper.PrintStr(line.text)
        posApiHelper.PrintStart()
        resetLine()
    }
}