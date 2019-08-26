package org.weilbach.splitbills.bills

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.amlcurran.showcaseview.ShowcaseView
import com.google.android.material.snackbar.Snackbar
import org.weilbach.splitbills.R
import org.weilbach.splitbills.data.Bill
import org.weilbach.splitbills.data.Group
import org.weilbach.splitbills.databinding.FragmentBillsBinding
import org.weilbach.splitbills.util.*
import android.view.LayoutInflater as LayoutInflater1

class BillsFragment : Fragment() {
    private lateinit var viewDataBinding: FragmentBillsBinding
    private lateinit var listAdapter: BillsAdapter

    private var groupName = ""

    override fun onCreateView(
        inflater: LayoutInflater1,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding =
                FragmentBillsBinding.inflate(inflater, container, false).apply {
                    viewmodel = (activity as BillsActivity).obtainViewModel()
                }
        setHasOptionsMenu(true)

        showShareGroupHint()

        return viewDataBinding.root
    }

    private fun showShareGroupHint() {
        if (getShowShareGroupHint(activity?.applicationContext)) {
            activity?.let {
                val toolbar = it.findViewById<Toolbar>(R.id.act_bill_toolbar)
                ShowcaseView.Builder(it)
                        .withMaterialShowcase()
                        .setTarget(ToolbarActionItemTarget(toolbar, R.id.menu_frag_bills_share))
                        .setStyle(R.style.CustomShowcaseTheme2)
                        .setContentText(getString(R.string.share_group_hint))
                        .build()
                        .show()
                setShowShareGroupHint(activity?.applicationContext, false)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val groupName = activity?.intent?.getStringExtra(BillsActivity.EXTRA_GROUP_NAME)
        groupName?.let {
            viewDataBinding.viewmodel?.start(groupName)
            this.groupName = groupName
            return
        }
        Log.w(TAG, "no group name set, can not start bills view model")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return viewDataBinding.viewmodel?.onOptionsItemSelected(item) ?: false
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.bills_fragment_menu, menu)
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
        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            listAdapter = BillsAdapter(viewModel, this)

            with(viewDataBinding.fragBillBillsList) {
                val decoration = DividerItemDecoration(
                        context,
                        (layoutManager as LinearLayoutManager).orientation)

                adapter = listAdapter
                addItemDecoration(decoration)
            }

            viewModel.items.observe(this, Observer<List<Bill>> { items ->
                activity?.applicationContext?.let { context ->
                    val list = items.map { item ->
                        BillItemViewModel(
                                item,
                                Group(groupName),
                                viewModel,
                                context,
                                AppExecutors(),
                                viewLifecycleOwner)
                    }
                    listAdapter.submitList(null)
                    listAdapter.submitList(list)
                }
            })

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
            scrollUpChild = viewDataBinding.fragBillBillsList
        }
    }

    companion object {
        fun newInstance() = BillsFragment()
        private const val TAG = "BillsFragment"

    }
}
