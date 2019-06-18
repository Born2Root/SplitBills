package org.weilbach.splitbills.addeditgroup

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_addeditbill.*
import kotlinx.android.synthetic.main.fragment_addeditgroup.*
import org.weilbach.splitbills.MemberItemNavigator
import org.weilbach.splitbills.R
import org.weilbach.splitbills.databinding.FragmentAddeditgroupBinding
import org.weilbach.splitbills.util.setupSnackbar


class AddEditGroupFragment : Fragment() {

    private lateinit var viewDataBinding: FragmentAddeditgroupBinding
    private lateinit var memberListAdapter: MemberAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpMemberListAdapter()
        // setupSaveButton()
        setupAddMemberButton()
        viewDataBinding.viewmodel?.let {
            view?.setupSnackbar(this, it.snackbarMessage, Snackbar.LENGTH_LONG)
        }
        setupActionBar()
        loadData()
    }

    /*override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        activity?.let {
            val inflater = it.menuInflater
            inflater.inflate(R.menu.menu_member_item, menu)
        }
        menu.setHeaderTitle("MemberData")
    }*/

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return viewDataBinding.viewmodel?.onContextItemSelected(item) ?: false
    }

    private fun loadData() {
        // Add or edit an existing task?
        //viewDataBinding.viewmodel?.start(arguments?.getString(ARGUMENT_EDIT_GROUP_NAME))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_addeditgroup, container, false)
        viewDataBinding = FragmentAddeditgroupBinding.bind(root).apply {
            viewmodel = (activity as AddEditGroupActivity).obtainViewModel()
        }
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        setHasOptionsMenu(true)
        retainInstance = false

        return viewDataBinding.root
    }

    /*override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_addeditgroup, menu)
    }*/

    private fun setUpMemberListAdapter() {
        viewDataBinding.viewmodel?.let {
            memberListAdapter = MemberAdapter(it, ArrayList(0), this)
            viewDataBinding.membersList.adapter = memberListAdapter
            return
        }
        Log.w(TAG, "ViewModel not initialized when attempting to set up adapter.")
    }

    /*private fun setupSaveButton() {
        activity?.findViewById<Button>(R.id.frag_add_edit_group_button_save)?.let {
            it.setOnClickListener { viewDataBinding.viewmodel?.saveGroup() }
        }
    }*/

    private fun setupAddMemberButton() {
        activity?.findViewById<Button>(R.id.frag_add_edit_group_button_add_member)?.let {
            it.setOnClickListener { viewDataBinding.viewmodel?.addMember() }
        }
    }

    private fun setupActionBar() {
        (activity as AppCompatActivity).supportActionBar?.setTitle(
                if (arguments != null && arguments?.get(ARGUMENT_EDIT_GROUP_NAME) != null)
                    R.string.frag_add_edit_group_edit_group
                else
                    R.string.frag_add_edit_group_add_group
        )
    }

    companion object {
        const val ARGUMENT_EDIT_GROUP_NAME = "EDIT_GROUP_NAME"
        private const val TAG = "AddEditGroupFragment"

        fun newInstance() = AddEditGroupFragment()
    }
}