package org.weilbach.splitbills.util

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.preference.PreferenceManager
import me.ibrahimsn.library.LiveSharedPreferences
import org.weilbach.splitbills.data.Member
import java.util.*

private const val TAG = "AppPreferences"

private const val KEY_USER_NAME = "user_name"
private const val KEY_USER_EMAIL = "user_email"
private const val KEY_CURRENCY = "currency_code"
private const val KEY_THEME = "theme"
private const val KEY_SHOW_SHARE_GROUP_DIALOG = "show_share_group_dialog"

private const val DEFAULT_USER_NAME = "no name set"
private const val DEFAULT_USER_EMAIL = "no email set"
private const val DEFAULT_CURRENCY = "EUR"
private const val DEFAULT_THEME = "day_night"
private const val DEFAULT_SHOW_SHARE_GROUP_DIALOG = true

fun getShowShareGroupDialog(context: Context?): Boolean {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    return prefs.getBoolean(KEY_SHOW_SHARE_GROUP_DIALOG, DEFAULT_SHOW_SHARE_GROUP_DIALOG)
}

fun setShowShareGroupDialog(context: Context?, value: Boolean) {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    prefs.edit()
            .putBoolean(KEY_SHOW_SHARE_GROUP_DIALOG, value)
            .apply()
}

fun getUser(context: Context?): Member {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    val name = prefs.getString("user_name", "no name set")
    val email = prefs.getString("user_email", "no email set")

    return Member(name, email)
}

fun getUserLive(context: Context?): LiveData<Member> {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    val liveSharedPreferences = LiveSharedPreferences(prefs)
    val name = liveSharedPreferences.getString(KEY_USER_NAME, DEFAULT_USER_NAME)
    val email = liveSharedPreferences.getString(KEY_USER_EMAIL, DEFAULT_USER_EMAIL)

    return MediatorLiveData<Member>().apply {
        addSource(name) { name ->
            email.value?.let { email ->
                value = Member(name, email)
            }
        }

        addSource(email) { email ->
            name.value?.let { name ->
                value = Member(name, email)
            }

        }
    }
}

fun getCurrencyLive(context: Context?): LiveData<String> {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    val liveSharedPreferences = LiveSharedPreferences(prefs)
    return liveSharedPreferences.getString(KEY_CURRENCY, DEFAULT_CURRENCY)
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

fun setTheme(context: Context?, value: String) {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    prefs.edit().putString(KEY_THEME, value).apply()
}

fun getTheme(context: Context?): String? {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    return prefs.getString(KEY_THEME, DEFAULT_THEME)
}