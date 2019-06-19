package org.weilbach.splitbills.util

import android.widget.LinearLayout
import android.os.Build
import android.annotation.TargetApi
import android.content.Context
import android.database.DataSetObserver
import android.util.AttributeSet
import android.widget.Adapter


/**
 * A linear layout that will contain views taken from an adapter. It differs
 * from the list view in the fact that it will not optimize anything and
 * draw all the views from the adapter. It also does not provide scrolling.
 * However, when you need a layout that will render views horizontally and
 * you know there are not many child views, this is a good option.
 *
 *
 * @author Vincent Mimoun-Prat @ MarvinLabs
 */
class AdapterLinearLayout : LinearLayout {

    var adapter: Adapter? = null
    set(value) {
        value?.let {
            field = it
            it.registerDataSetObserver(dataSetObserver)
            reloadChildViews()
        }
    }

    private val dataSetObserver = object : DataSetObserver() {
        override fun onChanged() {
            super.onChanged()
            reloadChildViews()
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        orientation = VERTICAL
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        orientation = VERTICAL
    }

    constructor(context: Context) : super(context) {
        orientation = VERTICAL
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (adapter != null) adapter!!.unregisterDataSetObserver(dataSetObserver)
    }

    private fun reloadChildViews() {
        removeAllViews()

        if (adapter == null) return

        val count = adapter!!.getCount()
        for (position in 0 until count) {
            val v = adapter!!.getView(position, null, this)
            if (v != null) addView(v)
        }

        requestLayout()
    }
}