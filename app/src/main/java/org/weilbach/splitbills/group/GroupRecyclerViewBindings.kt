package org.weilbach.splitbills.group

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView

object GroupRecyclerViewBindings {

    @BindingAdapter("app:items")
    @JvmStatic
    fun setItems(recyclerView: RecyclerView, items: List<GroupItemViewModel>?) {
        with(recyclerView.adapter as GroupAdapter) {
            items?.let {
                replaceData(it)
            }
        }
    }
}