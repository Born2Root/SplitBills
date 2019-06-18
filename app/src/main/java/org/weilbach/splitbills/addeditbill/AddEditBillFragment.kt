package org.weilbach.splitbills.addeditbill

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import org.weilbach.splitbills.R
import org.weilbach.splitbills.addeditgroup.GroupSpinnerAdapter
import org.weilbach.splitbills.addeditgroup.MemberAdapter
import org.weilbach.splitbills.data.Group
import org.weilbach.splitbills.databinding.FragmentAddeditbillBinding
import org.weilbach.splitbills.util.CurrencyAdapter
import org.weilbach.splitbills.util.setupSnackbar

class AddEditBillFragment : Fragment() {

    private lateinit var viewDataBinding: FragmentAddeditbillBinding
    private lateinit var groupSpinnerAdapter: GroupSpinnerAdapter
    private lateinit var groupArrayAdapter: ArrayAdapter<Group>
    private lateinit var debtorsListAdapter: MemberAdapter
    private lateinit var currencyAdapter: CurrencyAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpGroupSpinnerAdapter()
        setUpDebtorListAdapter()
        setUpCurrencyAdapter()
        viewDataBinding.viewmodel?.let {
            view?.setupSnackbar(this, it.snackbarMessage, Snackbar.LENGTH_LONG)
        }
        setupActionBar()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_addeditbill, container, false)
        viewDataBinding = FragmentAddeditbillBinding.bind(root).apply {
            viewmodel = (activity as AddEditBillActivity).obtainViewModel()
        }
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        setHasOptionsMenu(true)
        retainInstance = false
        return viewDataBinding.root
    }

    private fun setUpCurrencyAdapter() {
        viewDataBinding.viewmodel?.let {
            currencyAdapter = CurrencyAdapter(ArrayList(0))
            viewDataBinding.fragAddEditBillSpinnerCurrency.adapter = currencyAdapter
        }
    }

    private fun setUpGroupSpinnerAdapter() {
        viewDataBinding.viewmodel?.let {
            groupSpinnerAdapter = GroupSpinnerAdapter(ArrayList(0))
            viewDataBinding.fragAddEditBillSpinnerGroups.adapter = groupSpinnerAdapter
        }
    }

    private fun setUpGroupArrayAdapter() {
        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            groupArrayAdapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item)
            viewDataBinding.fragAddEditBillSpinnerGroups.adapter = groupArrayAdapter
        } else {
            Log.w(TAG, "ViewModel not initialized when attempting to set up adapter.")
        }
    }

    private fun setUpDebtorListAdapter() {
        viewDataBinding.viewmodel?.let {
            debtorsListAdapter = MemberAdapter(it, ArrayList(0), this)
            viewDataBinding.fragAddEditBillListViewDebtors.adapter = debtorsListAdapter
            return
        }
        Log.w(TAG, "ViewModel not initialized when attempting to set up adapter.")
    }

    private fun setupActionBar() {
        (activity as AppCompatActivity).supportActionBar?.setTitle(
                if (arguments != null && arguments?.get(ARGUMENT_EDIT_BILL_ID) != null)
                    R.string.frag_add_edit_bill_edit_bill
                else
                    R.string.frag_add_edit_bill_add_bill
        )
    }

    companion object {
        const val ARGUMENT_EDIT_BILL_ID = "EDIT_BILL_ID"
        private const val TAG = "AddEditBillFragment"

        fun newInstance() = AddEditBillFragment()
    }
}