package br.com.robsonldo.samplemyutils

import android.content.Context
import androidx.multidex.MultiDexApplication
import br.com.robsonldo.myutils.preference.PreferenceManager
import br.com.robsonldo.myutils.view.ViewFontDefault
import br.com.robsonldo.samplemyutils.preference.MyPreference

class Application : MultiDexApplication() {

    companion object {
        private var instance: Application? = null
        private lateinit var context: Context

        fun getInstance(): Application {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) instance = Application()
                }
            }

            return instance!!
        }
    }

    override fun onCreate() {
        context = applicationContext
        PreferenceManager.init(this, MyPreference())
        ViewFontDefault.init(R.font.roboto_black)
        super.onCreate()
    }

    fun getContext(): Context {
        return context
    }
}

fun PreferenceManager.iSelf() = i(MyPreference::class.java)