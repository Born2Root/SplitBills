package org.weilbach.splitbills.addeditgroup

import android.widget.ListView
import androidx.databinding.BindingAdapter
import org.weilbach.splitbills.balances.BalancesAdapter
import org.weilbach.splitbills.data2.Member

object MemberListBindings {
    @BindingAdapter("app:items")
    @JvmStatic
    fun setItems(listView: ListView, items: List<Member>?) {

        if (listView.adapter is MemberAdapter) {
            with(listView.adapter as MemberAdapter) {
                items?.let {
                    replaceData(it)
                }
            }
        }

        if (listView.adapter is BalancesAdapter) {
            with(listView.adapter as BalancesAdapter) {
                items?.let {
                    replaceData(it)
                }
            }
        }
    }
}
