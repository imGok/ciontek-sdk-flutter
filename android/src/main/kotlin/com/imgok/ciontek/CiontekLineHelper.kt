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
    fun print(line: PrintLine) {
        posApiHelper.PrintInit()
        initLine(line)
        posApiHelper.PrintStr(line.text)
        posApiHelper.PrintStart()
    }
}