package org.weilbach.splitbills.bills

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView

object BillsRecyclerViewBindings {

    @BindingAdapter("app:items")
    @JvmStatic
    fun setItems(recyclerView: RecyclerView, items: List<BillItemViewModel>?) {
        with(recyclerView.adapter as BillsAdapter) {
            items?.let {
                replaceData(it)
            }
        }
    }
}