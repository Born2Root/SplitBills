package org.weilbach.splitbills.bills

import org.weilbach.splitbills.data.Bill

interface BillItemUserActionsListener {
    fun onBillClicked(bill: Bill)
}