package org.weilbach.splitbills.bills

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.weilbach.splitbills.R
import org.weilbach.splitbills.databinding.BillItemBinding

class BillsAdapter(
        private val billsViewModel: BillsViewModel,
        private val parent: Fragment
) : ListAdapter<BillItemViewModel, BillsAdapter.ViewHolder>(BillItemViewModelDiffCallback()) {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(BillItemBinding.inflate(
                LayoutInflater.from(viewGroup.context), viewGroup, false), parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val billItemViewModel = getItem(position)
        holder.apply {
            bind(billItemViewModel, object : BillItemUserActionsListener {
                override fun onBillClicked(billItemViewModel: BillItemViewModel) {
                    billsViewModel.openBill(billItemViewModel.bill.id)
                }
            })
            itemView.tag = billItemViewModel
        }
    }

    fun replaceData(billItemViewModels: List<BillItemViewModel>) {
        submitList(null)
        submitList(billItemViewModels)
    }

    class ViewHolder(
            private val binding: BillItemBinding,
            private val parent: Fragment
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BillItemViewModel, userActionsListener: BillItemUserActionsListener) {
            binding.apply {
                parent.context?.let { context ->
                    root.setOnCreateContextMenuListener { menu, _, _ ->
                        menu?.add(0, 0, position, context.getString(R.string.remove))
                    }
                }
                listener = userActionsListener
                viewmodel = item
                lifecycleOwner = parent.viewLifecycleOwner
                executePendingBindings()
            }
        }
    }
}

private class BillItemViewModelDiffCallback : DiffUtil.ItemCallback<BillItemViewModel>() {

    override fun areItemsTheSame(oldItem: BillItemViewModel, newItem: BillItemViewModel): Boolean {
        return oldItem.bill.id == newItem.bill.id
    }

    override fun areContentsTheSame(oldItem: BillItemViewModel, newItem: BillItemViewModel): Boolean {
        // FIXME: This may lead to errors
        return oldItem.bill.id == newItem.bill.id
    }

}
