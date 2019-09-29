package org.weilbach.splitbills.group

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.weilbach.splitbills.R
import org.weilbach.splitbills.databinding.GroupItemBinding

class GroupAdapter(
        private val groupViewModel: GroupViewModel,
        private val parent: Fragment
) : ListAdapter<GroupItemViewModel, GroupAdapter.ViewHolder>(GroupItemViewModelDiffCallback()) {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(GroupItemBinding.inflate(
                LayoutInflater.from(viewGroup.context), viewGroup, false), parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val groupItemViewModel = getItem(position)
        holder.apply {
            bind(groupItemViewModel, object : GroupItemUserActionsListener {
                override fun onGroupClicked(groupItemViewModel: GroupItemViewModel) {
                    groupViewModel.openGroup(groupItemViewModel.groupName)
                }
            })
        }
    }

    fun replaceData(groupItemViewModels: List<GroupItemViewModel>) {
        submitList(null)
        submitList(groupItemViewModels)
    }

    class ViewHolder(
            private val binding: GroupItemBinding,
            private val parent: Fragment
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GroupItemViewModel, userActionsListener: GroupItemUserActionsListener) {
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

private class GroupItemViewModelDiffCallback : DiffUtil.ItemCallback<GroupItemViewModel>() {

    override fun areItemsTheSame(oldItem: GroupItemViewModel, newItem: GroupItemViewModel): Boolean {
        return oldItem.group.name == newItem.group.name
    }

    override fun areContentsTheSame(oldItem: GroupItemViewModel, newItem: GroupItemViewModel): Boolean {
        // FIXME: This may lead to errors
        return oldItem.group.name == newItem.group.name
    }

}