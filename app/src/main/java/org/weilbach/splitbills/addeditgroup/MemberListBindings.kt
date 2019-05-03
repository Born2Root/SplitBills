package org.weilbach.splitbills.addeditgroup

import android.widget.ListView
import androidx.databinding.BindingAdapter
import org.weilbach.splitbills.data.Member

object MemberListBindings {
    @BindingAdapter("app:items")
    @JvmStatic
    fun setItems(listView: ListView, items: List<Member>) {
        with(listView.adapter as MemberAdapter) {
            replaceData(items)
        }
    }
}