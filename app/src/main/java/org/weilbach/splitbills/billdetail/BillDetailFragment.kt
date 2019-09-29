package org.weilbach.splitbills.billdetail

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import org.weilbach.splitbills.R
import org.weilbach.splitbills.databinding.FragmentBilldetailBinding


class BillDetailFragment : Fragment() {

    private lateinit var viewDataBinding: FragmentBilldetailBinding

    private var billId = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewDataBinding =
                FragmentBilldetailBinding.inflate(inflater, container, false).apply {
                    viewmodel = (activity as BillDetailActivity).obtainViewModel()
                }
        setHasOptionsMenu(true)

        return viewDataBinding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_bill_detail_activity, menu)
        viewDataBinding.viewmodel?.bill?.observe(this, Observer { billDebtors ->
            activity?.invalidateOptionsMenu()
        })
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        val valid = viewDataBinding.viewmodel?.bill?.value?.bill?.valid ?: return
        val item = menu.findItem(R.id.menu_bill_detail_activity_remove)
        item?.isEnabled = valid
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
    }

    override fun onResume() {
        super.onResume()
        val billId = activity?.intent?.getStringExtra(BillDetailActivity.EXTRA_BILL_ID)
        billId?.let {
            viewDataBinding.viewmodel?.start(billId)
            this.billId = billId
        }
    }

    companion object {
        fun newInstance() = BillDetailFragment()
    }
}