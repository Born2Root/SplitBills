package org.weilbach.splitbills.bills

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import org.weilbach.splitbills.R
import org.weilbach.splitbills.data2.Bill
import org.weilbach.splitbills.data2.Group
import org.weilbach.splitbills.databinding.BillItemBinding
import org.weilbach.splitbills.util.AppExecutors
import java.lang.IllegalStateException

class BillsAdapter(
        private var bills: List<Bill>,
        private val billsViewModel: BillsViewModel,
        private val group: Group,
        private val parent: Fragment
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
            parent.context?.let { context ->
                viewmodel = BillItemViewModel(
                        bills[position],
                        group,
                        billsViewModel,
                        context,
                        AppExecutors(),
                        parent.viewLifecycleOwner
                )
                root.setOnCreateContextMenuListener { menu, _, _ ->
                    menu?.add(0, 0, position, context.getString(R.string.remove))
                }
            }
            bills[position] // ?
            listener = userActionsListener
            lifecycleOwner = parent.viewLifecycleOwner
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

    fun replaceData(billData: List<Bill>) {
        setList(billData)
    }

    private fun setList(billData: List<Bill>) {
        this.bills = billData
        notifyDataSetChanged()
    }
}