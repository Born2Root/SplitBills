package org.weilbach.splitbills.bills

import android.widget.ListView
import androidx.databinding.BindingAdapter
import org.weilbach.splitbills.data.Bill

object BillListBindings {

    @BindingAdapter("app:items")
    @JvmStatic
    fun setItems(listView: ListView, items: List<Bill>) {
        with(listView.adapter as BillsAdapter) {
            replaceData(items)
        }
    }
}