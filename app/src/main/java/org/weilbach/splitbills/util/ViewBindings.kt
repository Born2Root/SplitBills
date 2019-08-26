package org.weilbach.splitbills.util

import androidx.databinding.BindingAdapter
import org.weilbach.splitbills.addeditbill.MemberWithAmount
import org.weilbach.splitbills.addeditbill.MemberWithAmountAdapter

object ViewBindings {
    @BindingAdapter("app:items")
    @JvmStatic
    fun setItems(adapterLinearLayout: AdapterLinearLayout, items: LinkedHashMap<String, MemberWithAmount>?) {

        with(adapterLinearLayout.adapter as MemberWithAmountAdapter) {
            items?.let {
                replaceData(it)
            }
        }
    }

}