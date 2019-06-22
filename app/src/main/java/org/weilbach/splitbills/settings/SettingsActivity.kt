package org.weilbach.splitbills.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import org.weilbach.splitbills.R
import org.weilbach.splitbills.util.ThemeUtil
import org.weilbach.splitbills.util.setTheme
import java.util.*

class SettingsActivity : AppCompatActivity(),
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    private val themeUtil = ThemeUtil()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        themeUtil.onCreate(this)
        setContentView(R.layout.activity_settings)
        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.act_settings_frame_layout, SettingsFragment())
                    .commit()
        }

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
            }
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        themeUtil.onResume(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onSupportNavigateUp(): Boolean {
        if (supportFragmentManager.popBackStackImmediate()) {
            return true
        }
        return super.onSupportNavigateUp()
    }

    override fun onPreferenceStartFragment(
            caller: PreferenceFragmentCompat,
            pref: Preference
    ): Boolean {
        // Instantiate the new Fragment
        val args = pref.extras
        val fragment = supportFragmentManager.fragmentFactory.instantiate(
                classLoader,
                pref.fragment
        ).apply {
            arguments = args
            setTargetFragment(caller, 0)
        }
        // Replace the existing Fragment with the new Fragment
        supportFragmentManager.beginTransaction()
                .replace(R.id.act_settings_frame_layout, fragment)
                .addToBackStack(null)
                .commit()
        title = pref.title

        return true
    }

    /**
     * The root preference fragment that displays preferences that link to the other preference
     * fragments below.
     */
    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)

            val currencyCodeList = LinkedList<String>()
            val currencySymbolList = LinkedList<String>()
            val currencyPref = findPreference<ListPreference>("currency_code")
            Currency.getAvailableCurrencies().forEach { currency ->
                currencyCodeList.add(currency.currencyCode)
                currencySymbolList.add(currency.symbol)
            }
            currencyPref?.entries = currencySymbolList.toTypedArray()
            currencyPref?.entryValues = currencyCodeList.toTypedArray()
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            val themeStyles = findPreference<ListPreference>("theme")
            themeStyles?.setOnPreferenceChangeListener { preference, newValue ->
                setTheme(context, newValue.toString())
                activity?.let {
                    it.supportFragmentManager.popBackStack()
                    it.recreate()
                }
                true
            }
        }
    }
}