package org.weilbach.splitbills.util

import android.widget.Spinner
import androidx.databinding.BindingAdapter
import java.util.*

object CurrencySpinnerBindings {

    @BindingAdapter("app:items")
    @JvmStatic
    fun setItems(spinner: Spinner, items: List<Currency>?) {
        with(spinner.adapter as CurrencyAdapter) {
            items?.let {
                replaceData(it)
            }
        }
    }
}