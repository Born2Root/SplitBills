package org.weilbach.splitbills.balances

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import org.weilbach.splitbills.R
import org.weilbach.splitbills.data.Group
import org.weilbach.splitbills.databinding.FragmentBalancesBinding
import org.weilbach.splitbills.util.setupSnackbar

class BalancesFragment : Fragment() {

    private lateinit var viewDataBinding: FragmentBalancesBinding
    private lateinit var listAdapter: BalancesAdapter

    private var groupName = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewDataBinding =
                FragmentBalancesBinding.inflate(inflater, container, false).apply {
                    viewmodel = (activity as BalancesActivity).obtainViewModel()
                }

        setHasOptionsMenu(true)
        return viewDataBinding.root
    }

    override fun onResume() {
        super.onResume()
        val groupName = activity?.intent?.getStringExtra(BalancesActivity.EXTRA_GROUP_NAME)
        groupName?.let {
            viewDataBinding.viewmodel?.start(groupName)
            this.groupName = groupName
            return
        }
        Log.w(TAG, "no group name set, can not start bills view model")
    }

    override fun onOptionsItemSelected(item: MenuItem) = false

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_balances_fragment, menu)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewDataBinding.viewmodel?.let {
            view?.setupSnackbar(this, it.snackbarMessage, Snackbar.LENGTH_LONG)
        }
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        setupListAdapter()
        setupRefreshLayout()
    }

    private fun setupListAdapter() {
        activity?.intent?.getStringExtra(BalancesActivity.EXTRA_GROUP_NAME)?.let {
            groupName = it
            val viewModel = viewDataBinding.viewmodel
            if (viewModel != null) {
                listAdapter = BalancesAdapter(
                        Group(it),
                        viewModel,
                        ArrayList(0),
                        this)
                viewDataBinding.fragBalancesBalancesList.adapter = listAdapter
            } else {
                Log.w(TAG, "ViewModel not initialized when attempting to set up adapter.")
            }
            return
        }
        Log.w(TAG, "Could not setup list adapter, no group name given.")
    }

    private fun setupRefreshLayout() {
        viewDataBinding.fragBalancesRefreshLayout.run {
            setColorSchemeColors(
                    ContextCompat.getColor(requireActivity(), R.color.colorPrimary),
                    ContextCompat.getColor(requireActivity(), R.color.colorAccent),
                    ContextCompat.getColor(requireActivity(), R.color.colorPrimaryDark)
            )
            // Set the scrolling view in the custom SwipeRefreshLayout.
            scrollUpChild = viewDataBinding.fragBalancesBalancesList
        }
    }

    companion object {
        fun newInstance() = BalancesFragment()
        private const val TAG = "BalancesFragment"
    }
}