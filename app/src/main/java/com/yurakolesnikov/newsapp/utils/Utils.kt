package com.yurakolesnikov.newsapp.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*

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

fun String.formatDate(): String { // Format date to the familiar appearance
    return "${this[8]}${this[9]}.${this[5]}${this[6]}.${this[0]}${this[1]}${this[2]}${this[3]}"
}

fun Fragment.hideKeyboard() {
    view?.let {activity?.hideKeyboard(it)}
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}