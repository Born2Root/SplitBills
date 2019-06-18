package org.weilbach.splitbills.addeditgroup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import org.weilbach.splitbills.MemberItemNavigator
import org.weilbach.splitbills.R
import org.weilbach.splitbills.data2.Member
import org.weilbach.splitbills.databinding.MemberItemBinding

class MemberAdapter(
        private val memberItemNavigator: MemberItemNavigator,
        private var members: List<Member>,
        private val parent: Fragment) : BaseAdapter() {

    fun replaceData(memberData: List<Member>) {
        setList(memberData)
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
            viewmodel = members[position]
            navigator = memberItemNavigator
            parent.context?.let { context ->
                root.setOnCreateContextMenuListener { menu, _, _ ->
                    menu?.add(0, 0, position, context.getString(R.string.remove))
                }
            }
            executePendingBindings()
        }

        return binding.root
    }

    private fun setList(member: List<Member>) {
        this.members = member
        notifyDataSetChanged()
    }
}