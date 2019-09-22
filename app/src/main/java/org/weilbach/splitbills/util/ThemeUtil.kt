package org.weilbach.splitbills.util

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.*
import org.weilbach.splitbills.R


class ThemeUtil {

    private var currentTheme: Int = 0

    fun onCreate(activity: AppCompatActivity) {
        currentTheme = getSelectedTheme(activity)
        setDefaultNightMode(currentTheme)
    }

    fun onResume(activity: AppCompatActivity) {
        /*if (currentTheme != getSelectedTheme(activity)) {
            val intent = activity.intent
            activity.finish()
            OverridePendingTransition.invoke(activity)
            activity.startActivity(intent)
            OverridePendingTransition.invoke(activity)
        }*/
    }

    private fun getSelectedTheme(context: Context): Int {
        return when (getTheme(context)) {
            "light" -> MODE_NIGHT_NO
            "dark" -> MODE_NIGHT_YES
            "day_night" -> MODE_NIGHT_FOLLOW_SYSTEM
            else -> MODE_NIGHT_FOLLOW_SYSTEM
        }
    }

    private object OverridePendingTransition {
        internal operator fun invoke(activity: AppCompatActivity) {
            activity.overridePendingTransition(0, 0)
        }
    }
}
