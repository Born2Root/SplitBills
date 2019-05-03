package org.weilbach.splitbills.addeditgroup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import org.weilbach.splitbills.data.Member
import org.weilbach.splitbills.databinding.MemberItemBinding
import java.lang.IllegalStateException

class MemberAdapter(
        private var members: List<Member>,
        private val viewModel: AddEditGroupViewModel
) : BaseAdapter() {

    fun replaceData(members: List<Member>) {
        setList(members)
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

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup): View {
        val binding: MemberItemBinding
        binding = if (view == null) {
            val inflater = LayoutInflater.from(viewGroup.context)
            MemberItemBinding.inflate(inflater, viewGroup, false)
        } else {
            DataBindingUtil.getBinding(view) ?: throw IllegalStateException()
        }

        with(binding) {
            member = members[position]
            executePendingBindings()
        }

        return binding.root
    }

    private fun setList(members: List<Member>) {
        this.members = members
        notifyDataSetChanged()
    }
}