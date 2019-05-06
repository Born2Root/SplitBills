package org.weilbach.splitbills.bills

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import org.weilbach.splitbills.R
import org.weilbach.splitbills.databinding.FragmentBillsBinding
import org.weilbach.splitbills.util.setupSnackbar
import android.view.LayoutInflater as LayoutInflater1

class BillsFragment : Fragment() {
    private lateinit var viewDataBinding: FragmentBillsBinding
    private lateinit var listAdapter: BillsAdapter

    override fun onCreateView(inflater: LayoutInflater1, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewDataBinding =
                FragmentBillsBinding.inflate(inflater, container, false).apply {
                    viewmodel = (activity as BillsActivity).obtainViewModel()
                }
        setHasOptionsMenu(true)
        return viewDataBinding.root
    }

    override fun onResume() {
        super.onResume()
        viewDataBinding.viewmodel?.start()
    }

    override fun onOptionsItemSelected(item: MenuItem) = false

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.bills_fragment_menu, menu)
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

    }

    private fun setupListAdapter() {
        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            listAdapter = BillsAdapter(ArrayList(0), viewModel)
            viewDataBinding.fragBillBillsList.adapter = listAdapter
        } else {
            Log.w(TAG, "ViewModel not initialized when attempting to set up adapter.")
        }
    }

    private fun setupRefreshLayout() {
        viewDataBinding.fragBillsRefreshLayout.run {
            setColorSchemeColors(
                    ContextCompat.getColor(requireActivity(), R.color.colorPrimary),
                    ContextCompat.getColor(requireActivity(), R.color.colorAccent),
                    ContextCompat.getColor(requireActivity(), R.color.colorPrimaryDark)
            )
            // Set the scrolling view in the custom SwipeRefreshLayout.
            scrollUpChild = viewDataBinding.fragBillBillsList
        }
    }

    companion object {
        fun newInstance() = BillsFragment()
        private const val TAG = "BillsFragment"

    }
}