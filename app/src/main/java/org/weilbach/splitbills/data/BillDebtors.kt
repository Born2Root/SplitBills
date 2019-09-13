package org.weilbach.splitbills.data

import androidx.room.Embedded
import androidx.room.Relation

class BillDebtors {

    @Embedded
    lateinit var bill: Bill

    @Relation(parentColumn = "id", entityColumn = "bill_id")
    lateinit var debtors: List<Debtor>

    override fun equals(other: Any?): Boolean {
        if (other is BillDebtors) {
            if (bill != other.bill) {
                return false
            }
            debtors.forEach { debtor ->
                if (!other.debtors.contains(debtor)) {
                    return false
                }
            }
            other.debtors.forEach { debtor ->
                if (!debtors.contains(debtor)) {
                    return false
                }
            }
            return true
        }
        return false
    }

    override fun hashCode(): Int {
        var result = bill.hashCode()
        result = 31 * result + debtors.hashCode()
        return result
    }
}