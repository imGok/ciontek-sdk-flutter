package com.imgok.ciontek

import com.ctk.sdk.PosApiHelper

object CiontekPrintHelper {
    private val posApiHelper: PosApiHelper = PosApiHelper.getInstance()

    @Synchronized
    fun initLine(line: PrintLine) {
        posApiHelper.PrintSetBold(if (line.bold) 1 else 0)
        posApiHelper.PrintSetUnderline(if (line.underline) 1 else 0)
        posApiHelper.PrintSetGray(line.textGray)
        posApiHelper.PrintSetAlign(1)
    }

    @Synchronized
    fun printLine(line: PrintLine) {
        posApiHelper.PrintInit()
        initLine(line)
        posApiHelper.PrintStr(line.text)
        posApiHelper.PrintStart()
    }
}