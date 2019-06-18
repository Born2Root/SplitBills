package org.weilbach.splitbills.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import org.weilbach.splitbills.databinding.CurrencyItemBinding
import java.lang.IllegalStateException
import java.util.*

class CurrencyAdapter(
        private var currencies: List<Currency>
) : BaseAdapter() {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val binding: CurrencyItemBinding
        binding = if (view == null) {
            val inflater = LayoutInflater.from(parent.context)
            CurrencyItemBinding.inflate(inflater, parent, false)
        } else {
            DataBindingUtil.getBinding(view) ?: throw IllegalStateException()
        }

        with(binding) {
            currency = currencies[position]
            executePendingBindings()
        }

        return  binding.root
    }

    override fun getItem(position: Int): Any {
        return currencies[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return currencies.size
    }

    fun replaceData(currencies: List<Currency>) {
        setList(currencies)
    }

    private fun setList(currencies: List<Currency>) {
        this.currencies = currencies
        notifyDataSetChanged()
    }
}