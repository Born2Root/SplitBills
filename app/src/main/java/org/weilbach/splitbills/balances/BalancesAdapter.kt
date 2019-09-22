package org.weilbach.splitbills.balances

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import org.weilbach.splitbills.data.Group
import org.weilbach.splitbills.data.Member
import org.weilbach.splitbills.databinding.BalancesItemBinding
import org.weilbach.splitbills.util.AppExecutors

class BalancesAdapter(
        private val group: Group,
        private val balancesViewModel: BalancesViewModel,
        private var members: List<Member>,
        private val parent: Fragment
) : BaseAdapter() {

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup): View {
        val binding: BalancesItemBinding
        binding = if (view == null) {
            val inflater = LayoutInflater.from(viewGroup.context)

            BalancesItemBinding.inflate(inflater, viewGroup, false)
        } else {
            DataBindingUtil.getBinding(view) ?: throw IllegalStateException()
        }

        with(binding) {
            parent.context?.let { context ->
                viewmodel = BalancesItemViewModel(group,
                        members[position],
                        balancesViewModel,
                        AppExecutors(),
                        context)
            }
            lifecycleOwner = parent.viewLifecycleOwner
            executePendingBindings()
        }

        return binding.root
    }

    override fun getItem(position: Int): Any {
        return members[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return members.size
    }

    fun replaceData(member: List<Member>) {
        setList(member)
    }

    private fun setList(member: List<Member>) {
        this.members = member
        notifyDataSetChanged()
    }
}