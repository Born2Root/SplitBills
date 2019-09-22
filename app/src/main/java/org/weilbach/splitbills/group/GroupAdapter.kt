package org.weilbach.splitbills.group

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import org.weilbach.splitbills.R
import org.weilbach.splitbills.data.Group
import org.weilbach.splitbills.databinding.GroupItemBinding
import org.weilbach.splitbills.util.AppExecutors

class GroupAdapter(
        private var groups: List<Group>,
        private val groupViewModel: GroupViewModel,
        private val parent: Fragment
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
            override fun onGroupClicked(groupItemViewModel: GroupItemViewModel) {
                groupViewModel.openGroup(groupItemViewModel.groupName)
            }
        }

        with(binding) {
            parent.context?.let { context ->
                viewmodel = GroupItemViewModel(
                        groups[position],
                        groupViewModel,
                        AppExecutors(),
                        parent.viewLifecycleOwner,
                        context)

                root.setOnCreateContextMenuListener { menu, _, _ ->
                    menu?.add(0, 0, position, context.getString(R.string.remove))
                }
            }
            listener = userActionsListener
            lifecycleOwner = parent.viewLifecycleOwner
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