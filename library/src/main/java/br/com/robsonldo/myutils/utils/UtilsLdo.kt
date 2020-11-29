package br.com.robsonldo.myutils.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Patterns
import android.util.TypedValue
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.content.pm.PackageInfoCompat
import org.apache.commons.lang3.text.WordUtils
import java.io.*
import java.util.*
import java.util.regex.Pattern

object UtilsLdo {

    fun writeFileAppend(path: String, sBody: String): Boolean {
        return try {
            val gpxFile = File(path)
            val bW: BufferedWriter
            bW = BufferedWriter(FileWriter(gpxFile, true))
            bW.write(sBody)
            bW.newLine()
            bW.flush()
            bW.close()
            true
        } catch (e: IOException) {
            false
        }
    }

    fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun concatenateWithDivisor(divisor: String, vararg args: Any): String {
        val concatenate = StringBuilder()
        for (i in args.indices) {
            concatenate.append(if (i == 0) args[i].toString() else divisor + args[i].toString())
        }
        return concatenate.toString()
    }

    fun separateWithDivisor(divisor: String, value: String): Array<String> {
        return value.split(Pattern.quote(divisor)).toTypedArray()
    }

    fun lastString(value: String, count: Int): String {
        val aux = StringBuilder("")
        var i = value.length - 1
        var j = 0
        while (j < count && j < value.length) {
            aux.append(value[i])
            i--
            j++
        }
        return aux.reverse().toString()
    }

    fun firstString(value: String, count: Int): String {
        val aux = StringBuilder()
        var i = 0
        while (i < count && i < value.length) {
            aux.append(value[i])
            i++
        }
        return aux.toString()
    }

    fun setTimeout(callback: Callback, time: Long) {
        Thread(Runnable {
            try {
                Thread.sleep(time)
                callback.success()
            } catch (e: InterruptedException) {
                callback.error(e)
            }
        }).start()
    }

    fun setInterval(callback: Callback, time: Long) {
        Thread(Runnable {
            do {
                try {
                    Thread.sleep(time)
                } catch (e: InterruptedException) {
                    callback.error(e)
                    break
                }
            } while (callback.success())
        }).start()
    }

    fun runBackground(process: Process) {
        Thread(Runnable { process.run() }).start()
    }

    fun runBackgroundUI(activity: Activity, process: Process) {
        Thread(Runnable {
            if (activity.isFinishing) return@Runnable
            activity.runOnUiThread { process.run() }
        }).start()
    }

    @Throws(PackageManager.NameNotFoundException::class)
    fun versionName(context: Context): String {
        val manager = context.packageManager
        val info = manager.getPackageInfo(context.packageName, 0)
        return info.versionName
    }

    @Throws(PackageManager.NameNotFoundException::class)
    fun versionCode(context: Context): Long {
        val manager = context.packageManager
        val info = manager.getPackageInfo(context.packageName, 0)
        return PackageInfoCompat.getLongVersionCode(info)
    }

    fun bitmapInFile(context: Context, bitmap: Bitmap): File? {
        var file: File? = File(context.filesDir.path, Date().time.toString() + ".jpg")
        val fOut: OutputStream?

        file?.let {
            try {
                fOut = BufferedOutputStream(FileOutputStream(it))
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                fOut.flush()
                fOut.close()
            } catch (e: Exception) {
                file = null
            }
        }

        return file
    }

    fun saveResizedImage(context: Context, image: Bitmap): File? {
        var resizedFile: File? = File(context.filesDir.path, Date().time.toString() + ".jpg")
        val fOut: OutputStream?

        resizedFile?.let {
            try {
                fOut = BufferedOutputStream(FileOutputStream(it))
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                fOut.flush()
                fOut.close()
            } catch (e: Exception) {
                resizedFile = null
            }
        }

        return resizedFile
    }

    fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap {
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    fun getColorResourceInString(context: Context, r: Int): String {
        return "#" + Integer.toHexString(ContextCompat.getColor(context, r))
    }

    fun colorStateList(hex: String?): ColorStateList {
        return colorStateList(Color.parseColor(hex))
    }

    fun colorStateList(color: Int): ColorStateList {
        return ColorStateList(arrayOf(IntArray(0)), intArrayOf(color))
    }

    fun capitalizeFull(text: String?): String {
        return if (text == null) "" else WordUtils.capitalizeFully(text)
    }

    fun capitalize(text: String?): String {
        return if (text == null) "" else WordUtils.capitalize(text)
    }

    fun sameDay(d1: Calendar, d2: Calendar): Boolean {
        return d1[Calendar.DAY_OF_MONTH] == d2[Calendar.DAY_OF_MONTH]
                && d1[Calendar.MONTH] == d2[Calendar.MONTH]
                && d1[Calendar.YEAR] == d2[Calendar.YEAR]
    }

    fun validateColorHex(hex: String): String {
        if (!hex.matches(Regex("^#[0-9A-Fa-f]{1,8}$"))) return "#000000"
        else if (hex.length >= 7) return hex

        val l = hex[hex.length - 1]
        val hexBuilder = StringBuilder(hex)
        for (i in hexBuilder.length..6) hexBuilder.append(l)
        return hexBuilder.toString()
    }

    fun traceRoute(activity: Activity, lat: Double, lng: Double) {
        val gmmIntentUri = Uri.parse(String.format("google.navigation:q=%s,%s",
            lat.toString(), lng.toString()))

        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        if (activity.isFinishing) return
        activity.startActivity(mapIntent)
    }

    fun openUrlBrowser(activity: Activity, url: String?) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.addCategory(Intent.CATEGORY_BROWSABLE)
        intent.data = Uri.parse(url)
        activity.startActivity(intent)
    }

    fun dpToPx(context: Context, dp: Int): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(),
            context.resources.displayMetrics
        )
    }

    fun pxToDp(context: Context, px: Float): Float {
        return px / (context.resources.displayMetrics.densityDpi.toFloat()
                / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun rgbInHex(r: Int, g: Int, b: Int): String {
        return String.format("#%02x%02x%02x", r, g, b)
    }

    fun getDeviceMetrics(context: Context): DisplayMetrics? {
        val metrics = DisplayMetrics()
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        display.getMetrics(metrics)
        return metrics
    }

    fun callPhone(activity: Activity, number: String) {
        val uri = String.format("tel:%s", number.trim { it <= ' ' })
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse(uri)
        activity.startActivity(intent)
    }

    fun around10Down(number: Double): Double {
        return Math.floor(number * 100) / 100
    }

    interface Process {
        fun run(): Boolean
    }

    interface Callback {
        fun success(): Boolean
        fun error(e: Exception?)
    }

    fun calculateNoOfColumns(context: Context, dimension: Int): Int {
        val displayMetrics = context.resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        return (dpWidth / dimension).toInt()
    }
}