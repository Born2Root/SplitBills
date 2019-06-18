package org.weilbach.splitbills.addeditbill

import android.widget.Spinner
import androidx.databinding.BindingAdapter
import org.weilbach.splitbills.addeditgroup.GroupSpinnerAdapter
import org.weilbach.splitbills.data2.Group

object GroupSpinnerBindings {

    @BindingAdapter("app:items")
    @JvmStatic
    fun setItems(spinner: Spinner, items: List<Group>?) {
        with(spinner.adapter as GroupSpinnerAdapter) {
            items?.let {
                replaceData(it)
            }
        }
    }

    /*@BindingAdapter("android:selection")
    @JvmStatic
    fun setSelection(spinner: Spinner, pos: Int) {

    }*/
}