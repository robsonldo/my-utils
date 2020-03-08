package br.com.robsonldo.myutils.animation

import android.app.Activity
import br.com.robsonldo.myutils.R
import br.com.robsonldo.myutils.animation.enums.TransitionAnimation

object ActivityTransitionAnimation {

    fun animation(activity: Activity, animation: TransitionAnimation) {
        animationChoice(activity, animation)
    }

    private fun animationChoice(activity: Activity, animation: TransitionAnimation) {
        when (animation) {
            TransitionAnimation.FROM_LEFT_TO_RIGHT -> {
                activity.overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
            TransitionAnimation.FROM_RIGHT_TO_LEFT -> {
                activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
            }
            TransitionAnimation.FADE_IN_FADE_OUT -> {
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
            TransitionAnimation.FADE_TOP_TO_BOTTOM -> {
                activity.overridePendingTransition(R.anim.fade_in, R.anim.slide_top_to_bottom)
            }
            else -> return
        }
    }
}