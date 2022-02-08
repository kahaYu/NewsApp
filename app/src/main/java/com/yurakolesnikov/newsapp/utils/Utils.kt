package com.yurakolesnikov.newsapp.utils

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController

fun Activity.hideSystemUI() {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.insetsController?.let {
            // Default behavior is that if navigation bar is hidden, the system will "steal" touches
            // and show it again upon user's touch. We just want the user to be able to show the
            // navigation bar by swipe, touches are handled by custom code -> change system bar behavior.
            // Alternative to deprecated SYSTEM_UI_FLAG_IMMERSIVE.
            it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            // Finally, hide the system bars, alternative to View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            // and SYSTEM_UI_FLAG_FULLSCREEN.
            it.hide(WindowInsets.Type.navigationBars())
            //window.statusBarColor = getColor(R.color.transparent)
            //window.setDecorFitsSystemWindows(false)
        }
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
                // Do not let system steal touches for showing the navigation bar
                View.SYSTEM_UI_FLAG_IMMERSIVE // Убирает только низ. Верх постоянно включен.
                        // Hide the nav bar and status bar
                        //or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        //View.SYSTEM_UI_FLAG_FULLSCREEN // Убирает и низ и верх. Верх по свайпу появляется.
                        or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                // Keep the app content behind the bars even if user swipes them up
                //or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                //or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                //or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                )
        // make navbar translucent - do this already in hideSystemUI() so that the bar
        // is translucent if user swipes it up
    }
}

fun Double.roundToNextInt(): Int {
    when {
        this == 0.0 -> return 0
        this >= 0.0 -> {
            if (this > this.toInt()) {
                var result = this.toInt() + 1
                return result
            } else {
                var result = this.toInt()
                return result
            }
        }
        else -> throw Exception("Input for function roundToNextInt can't be less than 0")
    }
}