package org.weilbach.splitbills.data

import androidx.room.Embedded
import androidx.room.Relation

class GroupMembersBillsDebtors {

    @Embedded
    lateinit var group: Group

    @Relation(parentColumn = "name", entityColumn = "group_name")
    lateinit var members: List<GroupMember>

    @Relation(parentColumn = "name", entityColumn = "group_name", entity = Bill::class)
    lateinit var bills: List<BillDebtors>
}