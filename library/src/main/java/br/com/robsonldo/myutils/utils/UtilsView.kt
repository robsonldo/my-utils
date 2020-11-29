package br.com.robsonldo.myutils.utils

import android.app.Activity
import android.content.Intent
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.ColorInt
import br.com.robsonldo.myutils.animation.ActivityTransitionAnimation
import br.com.robsonldo.myutils.animation.enums.TransitionAnimation


object UtilsActivity {

    fun goToActivity(
        activity: Activity, destination: Class<*>, bundle: Bundle? = null,
        animation: TransitionAnimation? = null, isFinish: Boolean = false
    ) {

        if (activity.isFinishing) return

        activity.runOnUiThread {
            if (bundle != null) {
                activity.startActivity(Intent(activity, destination).putExtras(bundle))
            } else {
                activity.startActivity(Intent(activity, destination))
            }

            if (animation != null) {
                ActivityTransitionAnimation.animation(activity, animation)
            }
            if (isFinish) activity.finish()
        }
    }

    fun menuIconColor(menuItem: MenuItem, @ColorInt color: Int) {
        val drawable = menuItem.icon
        if (drawable != null) {
            drawable.mutate()
            setDrawableColorFilter(drawable, color);
        }
    }

    @Suppress("DEPRECATION")
    fun setDrawableColorFilter(drawable: Drawable, @ColorInt color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
        } else {
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
    }
}