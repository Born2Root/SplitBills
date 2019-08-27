package org.weilbach.splitbills.addeditbill

import org.weilbach.splitbills.data.Bill

interface AddEditBillNavigator {
    fun onBillSaved(bill: Bill)
}