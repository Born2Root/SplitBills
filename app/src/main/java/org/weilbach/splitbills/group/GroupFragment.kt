package org.weilbach.splitbills.group

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import org.weilbach.splitbills.R

import org.weilbach.splitbills.databinding.FragmentGroupBinding
import org.weilbach.splitbills.util.setupSnackbar

class GroupFragment : Fragment() {
    private lateinit var viewDataBinding: FragmentGroupBinding
    private lateinit var listAdapter: GroupAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewDataBinding =
                FragmentGroupBinding.inflate(inflater, container, false).apply {
                    viewmodel = (activity as GroupActivity).obtainViewModel()
                }
        setHasOptionsMenu(true)
        return viewDataBinding.root
    }

    override fun onResume() {
        super.onResume()
        viewDataBinding.viewmodel?.start()
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
                /*R.id.menu_clear -> {
                    viewDataBinding.viewmodel?.clearCompletedTasks()
                    true
                }
                R.id.menu_filter -> {
                    showFilteringPopUpMenu()
                    true
                }
                R.id.menu_refresh -> {
                    viewDataBinding.viewmodel?.loadTasks(true)
                    true
                }*/
                else -> false
            }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.groups_fragment_menu, menu)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewDataBinding.viewmodel?.let {
            view?.setupSnackbar(this, it.snackbarMessage, Snackbar.LENGTH_LONG)
        }
        viewDataBinding.setLifecycleOwner(this.viewLifecycleOwner)
        setupFab()
        setupListAdapter()
        setupRefreshLayout()
    }

    private fun setupFab() {
        /*activity?.findViewById<FloatingActionButton>(R.id.fab_add_task)?.let {
            it.setImageResource(R.drawable.ic_add)
            it.setOnClickListener {
                viewDataBinding.viewmodel?.addNewTask()
            }
        }*/
    }

    private fun setupListAdapter() {
        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            listAdapter = GroupAdapter(ArrayList(0), viewModel)
            viewDataBinding.fragGroupGroupsList.adapter = listAdapter
        } else {
            Log.w(TAG, "ViewModel not initialized when attempting to set up adapter.")
        }
    }

    private fun setupRefreshLayout() {
        viewDataBinding.fragGroupRefreshLayout.run {
            setColorSchemeColors(
                    ContextCompat.getColor(requireActivity(), R.color.colorPrimary),
                    ContextCompat.getColor(requireActivity(), R.color.colorAccent),
                    ContextCompat.getColor(requireActivity(), R.color.colorPrimaryDark)
            )
            // Set the scrolling view in the custom SwipeRefreshLayout.
            scrollUpChild = viewDataBinding.fragGroupGroupsList
        }
    }

    companion object {
        fun newInstance() = GroupFragment()
        private const val TAG = "GroupFragment"

    }
}