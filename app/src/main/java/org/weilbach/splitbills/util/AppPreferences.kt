package org.weilbach.splitbills.util

import android.content.Context
import androidx.preference.PreferenceManager
import android.util.Log
import org.weilbach.splitbills.data.Member
import java.util.*

private const val TAG = "AppPreferences"

fun getUser(context: Context?): Member {
    if (context == null) {
        Log.d(TAG, "context is null")
    }
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    val name = prefs.getString("user_name", "no name set")
    val email = prefs.getString("user_email", "no email set")

    return Member(name, email)
}

fun setUser(context: Context?, member: Member) {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    prefs.edit()
            .putString("user_name", member.name)
            .putString("user_email", member.email)
            .apply()
}

fun getShowImportGroupHint(context: Context?): Boolean {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    return prefs.getBoolean("show_import_group_hint", true)
}

fun setShowImportGroupHint(context: Context?, value: Boolean) {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    prefs.edit()
            .putBoolean("show_import_group_hint", value)
            .apply()
}

fun getShowShareGroupHint(context: Context?): Boolean {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    return prefs.getBoolean("show_share_group_hint", true)
}

fun setShowShareGroupHint(context: Context?, value: Boolean) {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    prefs.edit()
            .putBoolean("show_share_group_hint", value)
            .apply()
}

fun getCurrency(context: Context?): Currency {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    val currencyCode = prefs.getString("currency_code", "EUR")
    return Currency.getInstance(currencyCode)
}

fun setCurrency(context: Context?, currency: Currency) {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    prefs.edit().putString("currency_code", currency.currencyCode).apply()
}

fun getFirstStart(context: Context?): Boolean {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    return prefs.getBoolean("first_start", true)
}

fun setFirstStart(context: Context?, value: Boolean) {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    prefs.edit().putBoolean("first_start", value).apply()
}