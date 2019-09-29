package org.weilbach.splitbills.billdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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