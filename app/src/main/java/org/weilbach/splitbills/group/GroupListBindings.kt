package org.weilbach.splitbills.group

import android.widget.ListView
import androidx.databinding.BindingAdapter
import org.weilbach.splitbills.data.Group

object GroupListBindings {

    @BindingAdapter("app:items")
    @JvmStatic
    fun setItems(listView: ListView, items: List<Group>) {
        with(listView.adapter as GroupAdapter) {
            replaceData(items)
        }
    }
}