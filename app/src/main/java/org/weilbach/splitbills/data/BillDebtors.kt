package org.weilbach.splitbills.data

import androidx.room.Embedded
import androidx.room.Relation

class BillDebtors {

    @Embedded
    lateinit var bill: Bill

    @Relation(parentColumn = "id", entityColumn = "bill_id")
    lateinit var debtors: List<Debtor>
}