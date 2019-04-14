package org.weilbach.splitbills.addeditgroup

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import org.weilbach.splitbills.R
import org.weilbach.splitbills.databinding.FragmentAddeditgroupBinding
import org.weilbach.splitbills.util.setupSnackbar

class AddEditGroupFragment : Fragment() {

    private lateinit var viewDataBinding: FragmentAddeditgroupBinding

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupFab()
        viewDataBinding.viewmodel?.let {
            view?.setupSnackbar(this, it.snackbarMessage, Snackbar.LENGTH_LONG)
        }
        setupActionBar()
        loadData()
    }

    private fun loadData() {
        // Add or edit an existing task?
        viewDataBinding.viewmodel?.start(arguments?.getString(ARGUMENT_EDIT_GROUP_NAME))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_addeditgroup, container, false)
        viewDataBinding = FragmentAddeditgroupBinding.bind(root).apply {
            viewmodel = (activity as AddEditGroupActivity).obtainViewModel()
        }
        viewDataBinding.setLifecycleOwner(this.viewLifecycleOwner)
        setHasOptionsMenu(true)
        retainInstance = false
        return viewDataBinding.root
    }

    /*override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_addeditgroup, menu)
    }*/

    private fun setupFab() {
        /*activity?.findViewById<FloatingActionButton>(R.id.fab_edit_task_done)?.let {
            it.setImageResource(R.drawable.ic_done)
            it.setOnClickListener { viewDataBinding.viewmodel?.saveTask() }
        }*/
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

        fun newInstance() = AddEditGroupFragment()
    }
}