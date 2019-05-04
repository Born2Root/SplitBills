package org.weilbach.splitbills.group

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import org.weilbach.splitbills.data.Group
import org.weilbach.splitbills.databinding.GroupItemBinding

class GroupAdapter(
        private var groups: List<Group>,
        private val groupViewModel: GroupViewModel
) : BaseAdapter() {

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup): View {
        val binding: GroupItemBinding
        binding = if (view == null) {
            // Inflate
            val inflater = LayoutInflater.from(viewGroup.context)

            // Create the binding
            GroupItemBinding.inflate(inflater, viewGroup, false)
        } else {
            // Recycling view
            DataBindingUtil.getBinding(view) ?: throw IllegalStateException()
        }

        val userActionsListener = object : GroupItemUserActionsListener {
            override fun onGroupClicked(group: Group) {
                groupViewModel.openGroup(group.name)
            }
        }

        with(binding) {
            group = groups[position]
            listener = userActionsListener
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

    fun replaceData(groups: List<Group>) {
        setList(groups)
    }

    private fun setList(groups: List<Group>) {
        this.groups = groups
        notifyDataSetChanged()
    }
}