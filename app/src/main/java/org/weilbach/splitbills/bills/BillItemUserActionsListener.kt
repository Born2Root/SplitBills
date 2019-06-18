package org.weilbach.splitbills.bills

import org.weilbach.splitbills.data2.Bill


interface BillItemUserActionsListener {
    fun onBillClicked(bill: Bill)
}