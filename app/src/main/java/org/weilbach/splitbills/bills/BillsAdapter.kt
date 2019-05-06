package org.weilbach.splitbills.bills

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import org.weilbach.splitbills.data.Bill
import org.weilbach.splitbills.databinding.BillItemBinding
import java.lang.IllegalStateException

class BillsAdapter(
        private var bills: List<Bill>,
        private val billsViewModel: BillsViewModel
        ) : BaseAdapter() {

        override fun getView(position: Int, view: View?, viewGroup: ViewGroup): View {
                val binding: BillItemBinding
                binding = if (view == null) {
                        val inflater = LayoutInflater.from(viewGroup.context)

                        BillItemBinding.inflate(inflater, viewGroup, false)
                } else {
                        DataBindingUtil.getBinding(view) ?: throw IllegalStateException()
                }

                val userActionsListener = object : BillItemUserActionsListener {
                        override fun  onBillClicked(bill: Bill) {
                                billsViewModel.openBill(bill.id)
                        }
                }

                with(binding) {
                        bill = bills[position]
                        listener = userActionsListener
                        executePendingBindings()
                }

                return binding.root
        }

        override fun getItem(position: Int): Any {
                return bills[position]
        }

        override fun getItemId(position: Int): Long {
                return position.toLong()
        }

        override fun getCount(): Int {
                return bills.size
        }

        fun replaceData(bills: List<Bill>) {
                setList(bills)
        }

        private fun setList(bills: List<Bill>) {
                this.bills = bills
                notifyDataSetChanged()
        }
}