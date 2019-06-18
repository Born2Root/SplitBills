package org.weilbach.splitbills.addeditgroup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import org.weilbach.splitbills.data2.Group
import org.weilbach.splitbills.databinding.GroupSpinnerItemBinding
import java.lang.IllegalStateException

class GroupSpinnerAdapter(
        private var groups: List<Group>
) : BaseAdapter() {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val binding: GroupSpinnerItemBinding
        binding = if (view == null) {
            val inflater = LayoutInflater.from(parent.context)
            GroupSpinnerItemBinding.inflate(inflater, parent, false)
        } else {
            DataBindingUtil.getBinding(view) ?: throw IllegalStateException()
        }

        with(binding) {
            group = groups[position]
            executePendingBindings()
        }

        return binding.root
    }

    override fun getItem(position: Int): Any {
        return groups[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return groups.size
    }

    fun replaceData(groupData: List<Group>) {
        setList(groupData)
    }

    private fun setList(groupData: List<Group>) {
        this.groups = groupData
        notifyDataSetChanged()
    }
}