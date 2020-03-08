package br.com.robsonldo.myutils.view

import androidx.annotation.FontRes

class ViewFontDefault {

    @FontRes
    var fontRes: Int = -1

    companion object {
        private var instance: ViewFontDefault? = null

        @JvmStatic
        fun getInstance(): ViewFontDefault {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) instance = ViewFontDefault()
                }
            }

            return instance!!
        }

        @JvmStatic
        fun init(@FontRes fontRes: Int) {
            getInstance().fontRes = fontRes
        }
    }
}