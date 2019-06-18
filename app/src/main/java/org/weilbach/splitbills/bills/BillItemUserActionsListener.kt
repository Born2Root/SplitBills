package org.weilbach.splitbills.bills

interface BillItemUserActionsListener {
    fun onBillClicked(billItemViewModel: BillItemViewModel)
}