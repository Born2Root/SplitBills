package org.weilbach.splitbills.data2

import androidx.room.Embedded
import androidx.room.Relation
import androidx.room.Transaction

class GroupBillsDebtors {

    @Embedded
    lateinit var group: Group

    @Relation(parentColumn = "name", entityColumn = "group_name", entity = Bill::class)
    lateinit var bills: List<BillDebtors>
}