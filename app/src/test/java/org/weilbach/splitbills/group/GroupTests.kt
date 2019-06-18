package org.weilbach.splitbills.group

import junit.framework.Assert.fail
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.weilbach.splitbills.*
import org.weilbach.splitbills.util.BillContainer
import org.weilbach.splitbills.util.MemberContainer

class GroupTests {

    private lateinit var group1: Group
    private lateinit var group1ToMerge: Group
    private lateinit var member1: Member
    private lateinit var member2: Member
    private lateinit var member3: Member
    private lateinit var bill1: Bill

    @Before
    fun setUpGroups() {
        member1 = Member("name1", "mail1@mail.org")
        member2 = Member("name2", "mail2@mail.org")
        member3 = Member("name3", "mail3@mail.org")

        // ------ Group1
        group1 = Group("group1", MemberContainer(), BillContainer())
        group1.members.add(member1)
        group1.members.add(member2)
        group1.members.add(member3)

        bill1 = Bill(
                "00:00",
                "bill1",
                "7.00",
                "EUR",
                MemberContainer(),
                member1.id,
                "group",
                true)
        bill1.debtors.add(member2)
        bill1.debtors.add(member3)
        group1.bills.add(bill1)

        // ------- Group1ToMerge
        group1ToMerge = Group("group1", MemberContainer(), BillContainer())
        group1ToMerge.bills.add(bill1)
    }

    @Test
    fun mergeGroups_groupsNotEqualId_mergeGroups() {
        var errorCalled = false
        val group2 = Group("group2", MemberContainer(), BillContainer())

        mergeGroups(group1, group2, object : GroupMergeCallback {
            override fun success(group: Group) {
                fail()
            }

            override fun error(group: Group) {
                errorCalled = true
            }

            override fun memberAdded(group: Group, member: Member) {
                fail()
            }

            override fun billAdded(group: Group, bill: Bill) {
                fail()
            }

            override fun billRemoved(group: Group, bill: Bill) {
                fail()
            }
        })

        Assert.assertEquals(errorCalled, true)
    }

    @Test
    fun mergeGroups_groupToMergeAdditionalMember_AddMember() {
        var member4Added = false
        var successCalled = false
        val member4 = Member("name4", "mail4@mail.org")
        group1ToMerge.members.add(member4)

        mergeGroups(group1, group1ToMerge, object : GroupMergeCallback {

            override fun success(group: Group) {
                successCalled = true
            }

            override fun error(group: Group) {
                fail()
            }

            override fun memberAdded(group: Group, member: Member) {
                if (group.id == group1.id && member.id == member4.id) {
                    member4Added = true
                }
            }

            override fun billAdded(group: Group, bill: Bill) {
                fail()
            }

            override fun billRemoved(group: Group, bill: Bill) {
                fail()
            }
        })

        Assert.assertEquals(true, successCalled)
        Assert.assertEquals(true, member4Added)
        Assert.assertEquals(true, group1.members.contains(member1))
        Assert.assertEquals(true, group1.members.contains(member2))
        Assert.assertEquals(true, group1.members.contains(member3))
        Assert.assertEquals(true, group1.members.contains(member4))
    }

    @Test
    fun mergeGroups_groupToMergeAdditionalBill_AddBill() {
        var successCalled = false
        var bill2Added = false
        val bill2 = Bill(
                "00:01",
                "bill2",
                "8.00",
                "EUR",
                MemberContainer(),
                "default",
                group1.id,
                true)

        group1ToMerge.bills.add(bill2)
        mergeGroups(group1, group1ToMerge, object : GroupMergeCallback {
            override fun billAdded(group: Group, bill: Bill) {
                if (bill.id == bill2.id) {
                    bill2Added = true
                }
            }

            override fun billRemoved(group: Group, bill: Bill) {
                fail()
            }

            override fun memberAdded(group: Group, member: Member) {
                fail()
            }

            override fun error(group: Group) {
                fail()
            }

            override fun success(group: Group) {
                successCalled = true
            }
        })

        Assert.assertEquals(true, successCalled)
        Assert.assertEquals(true, bill2Added)
        Assert.assertEquals(true, group1.bills.contains(bill1))
        Assert.assertEquals(true, group1.bills.contains(bill2))

    }

    @Test
    fun mergeGroups_groupToMergeNoDifference_DoNothing() {
        var successCalled = false
        group1ToMerge.members.add(member1)
        group1ToMerge.members.add(member2)
        group1ToMerge.members.add(member3)
        group1ToMerge.bills.add(bill1)

        mergeGroups(group1, group1ToMerge, object: GroupMergeCallback {
            override fun success(group: Group) {
                successCalled = true
            }

            override fun error(group: Group) {
                fail()
            }

            override fun memberAdded(group: Group, member: Member) {
                fail()
            }

            override fun billAdded(group: Group, bill: Bill) {
                fail()
            }

            override fun billRemoved(group: Group, bill: Bill) {
                fail()
            }

        })

        Assert.assertEquals(true, successCalled)
        Assert.assertEquals(true, group1.bills.contains(bill1))
        Assert.assertEquals(true, group1.members.contains(member1))
        Assert.assertEquals(true, group1.members.contains(member2))
        Assert.assertEquals(true, group1.members.contains(member3))
    }

    @Test
    fun mergeGroups_groupToMergeRemovedBill_removeBill() {
        var successCalled = false
        var bill2Removed = false

        val memberContainer = MemberContainer()
        memberContainer.add(member1)

        val bill2 = Bill(
                "00:00",
                "bill2",
                "7.00",
                "EUR",
                MemberContainer(),
                member1.id,
                "group",
                true)
        group1.bills.add(bill2)

        val bill2ToMerge = Bill(
                "00:00",
                "bill2",
                "3.00",
                "DOL",
                memberContainer,
                member1.id,
                "group",
                false)
        group1ToMerge.bills.add(bill2ToMerge)

        mergeGroups(group1, group1ToMerge, object : GroupMergeCallback {
            override fun success(group: Group) {
                successCalled = true
            }

            override fun error(group: Group) {
                fail()
            }

            override fun memberAdded(group: Group, member: Member) {
                fail()
            }

            override fun billAdded(group: Group, bill: Bill) {
                fail()
            }

            override fun billRemoved(group: Group, bill: Bill) {
                if (bill.id == bill2.id) {
                    bill2Removed = true
                }
            }

        })

        Assert.assertEquals(true, successCalled)
        Assert.assertEquals(true, bill2Removed)
        Assert.assertEquals(true, group1.bills.contains(bill1))
        Assert.assertEquals(0, group1.bills.get(bill2.id)?.debtors?.size)
        Assert.assertEquals("7.00", group1.bills.get(bill2.id)?.amount)
        Assert.assertEquals("EUR", group1.bills.get(bill2.id)?.currency)
        Assert.assertEquals(false, group1.bills.get(bill2.id)?.valid)
    }

    @Test
    fun mergeGroups_groupToMergeBillValid_doNothing() {
        var successCalled = false

        val bill2 = Bill("00:01",
                "bill2",
                "8.00",
                "EUR",
                MemberContainer(),
                "default",
                group1.id,
                false)

        val bill2ToMerge = Bill("00:01",
                "bill2",
                "8.00",
                "EUR",
                MemberContainer(),
                "default",
                group1.id,
                true)

        group1.bills.add(bill2)
        group1ToMerge.bills.add(bill2ToMerge)

        mergeGroups(group1, group1ToMerge, object : GroupMergeCallback {
            override fun success(group: Group) {
                successCalled = true
            }

            override fun error(group: Group) {
                fail()
            }

            override fun memberAdded(group: Group, member: Member) {
                fail()
            }

            override fun billAdded(group: Group, bill: Bill) {
                fail()
            }

            override fun billRemoved(group: Group, bill: Bill) {
                fail()
            }

        })

        Assert.assertEquals(true, successCalled)
        Assert.assertEquals(true, group1.bills.contains(bill1))
        Assert.assertEquals(false, group1.bills.get(bill2.id)?.valid)
    }

}