package org.weilbach.splitbills.group

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.amlcurran.showcaseview.ShowcaseView
import com.google.android.material.snackbar.Snackbar
import org.weilbach.splitbills.R
import org.weilbach.splitbills.databinding.FragmentGroupBinding
import org.weilbach.splitbills.util.*

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

        showImportGroupHint()

        return viewDataBinding.root
    }

    private fun showImportGroupHint() {
        if (getShowImportGroupHint(activity?.applicationContext)) {
            activity?.let {
                val toolbar = it.findViewById<Toolbar>(R.id.act_group_toolbar)
                ShowcaseView.Builder(it)
                        .withMaterialShowcase()
                        .setTarget(ToolbarActionItemTarget(toolbar, R.id.menu_frag_groups_import))
                        .setStyle(R.style.CustomShowcaseTheme2)
                        .setContentText(getString(R.string.import_group_hint))
                        .build()
                        .show()
                setShowImportGroupHint(activity?.applicationContext, false)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.groups_fragment_menu, menu)

        val viewModel = (activity as GroupActivity).obtainViewModel()
        viewModel.apply {
            groupMergeStartedEvent.observe(this@GroupFragment, Observer<Event<Unit>> {
                menu.findItem(R.id.menu_frag_groups_import)?.isVisible = false
                activity?.invalidateOptionsMenu()
            })

            groupMergeFailed.observe(this@GroupFragment, Observer<Event<Unit>> {
                menu.findItem(R.id.menu_frag_groups_import)?.isVisible = true
                activity?.invalidateOptionsMenu()
            })

            groupAddedEvent.observe(this@GroupFragment, Observer<Event<Unit>> {
                menu.findItem(R.id.menu_frag_groups_import)?.isVisible = true
                activity?.invalidateOptionsMenu()
            })

            groupMergedEvent.observe(this@GroupFragment, Observer<Event<Unit>> {
                menu.findItem(R.id.menu_frag_groups_import)?.isVisible = true
                activity?.invalidateOptionsMenu()
            })
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewDataBinding.viewmodel?.let {
            view?.setupSnackbar(this, it.snackbarMessage, Snackbar.LENGTH_LONG)
        }
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner

        setupListAdapter()
    }

    private fun setupListAdapter() {
        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            listAdapter = GroupAdapter(viewModel, this)

            with(viewDataBinding.fragmentGroupRecyclerviewGroups) {
                val decoration = DividerItemDecoration(
                        context,
                        (layoutManager as LinearLayoutManager).orientation)

                adapter = listAdapter
                addItemDecoration(decoration)
            }
        } else {
            Log.w(TAG, "ViewModel not initialized when attempting to set up adapter.")
        }
    }

    companion object {
        fun newInstance() = GroupFragment()
        private const val TAG = "GroupFragment"

    }
}