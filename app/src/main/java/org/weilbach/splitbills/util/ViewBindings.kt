package org.weilbach.splitbills.util

import androidx.databinding.BindingAdapter
import org.weilbach.splitbills.addeditbill.DebtorItemViewModel
import org.weilbach.splitbills.addeditbill.DebtorItemViewModelAdapter

object ViewBindings {
    @BindingAdapter("app:items")
    @JvmStatic
    fun setItems(adapterLinearLayout: AdapterLinearLayout, items: List<DebtorItemViewModel>?) {

        with(adapterLinearLayout.adapter as DebtorItemViewModelAdapter) {
            items?.let {
                replaceData(it)
            }
        }
    }

}