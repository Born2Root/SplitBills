package org.weilbach.splitbills

import org.junit.Assert
import org.junit.Test
import org.weilbach.splitbills.data.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class BalanceTests {

    @Test
    fun testMemberGetsFromGroup_AllMembersInGroup() {
        val res1 = memberGetsFromGroup(MEMBER1, GROUP_MEMBERS_BILLS_DEBTORS)
        val res2 = memberGetsFromGroup(MEMBER2, GROUP_MEMBERS_BILLS_DEBTORS)
        val res3 = memberGetsFromGroup(MEMBER3, GROUP_MEMBERS_BILLS_DEBTORS)

        Assert.assertEquals("Balance map member 1 size", 2, res1.size)
        Assert.assertEquals("Balance map member 2 size", 2, res2.size)
        Assert.assertEquals("Balance map member 3 size", 0, res3.size)

        Assert.assertEquals("member 2 in balance map of member 1", true, res1.containsKey(MEMBER2.email))
        Assert.assertEquals("member 3 in balance map of member 1", true, res1.containsKey(MEMBER3.email))

        Assert.assertEquals("member 1 in balance map of member 2", true, res2.containsKey(MEMBER1.email))
        Assert.assertEquals("member 3 in balance map of member 2", true, res2.containsKey(MEMBER3.email))

        Assert.assertEquals(
                "member 1 gets from member 2",
                BigDecimal(6.47).setScale(2, RoundingMode.UP),
                res1[MEMBER2.email]?.setScale(2, RoundingMode.UP))
        Assert.assertEquals(
                "member 1 gets from member 3",
                BigDecimal(1.47).setScale(2, RoundingMode.UP),
                res1[MEMBER3.email]?.setScale(2, RoundingMode.UP))

        Assert.assertEquals(
                "member 2 gets from member 1",
                BigDecimal(11.1).setScale(2, RoundingMode.UP),
                res2[MEMBER1.email]?.setScale(2, RoundingMode.UP))
        Assert.assertEquals(
                "member 2 gets from member 3",
                BigDecimal(11.1).setScale(2, RoundingMode.UP),
                res2[MEMBER3.email]?.setScale(2, RoundingMode.UP))
    }

    @Test
    fun testMemberGetsFromGroup_MemberNotInGroup() {
        val memberNotInGroup = Member("name", "email")
        val res = memberGetsFromGroup(memberNotInGroup, GROUP_MEMBERS_BILLS_DEBTORS)

        Assert.assertEquals("no members in balance map", 0, res.size)
    }

    @Test
    fun testMemberGetsFromGroupTotal() {
        val res1 = memberGetsFromGroupTotal(MEMBER1, GROUP_MEMBERS_BILLS_DEBTORS)
        val res2 = memberGetsFromGroupTotal(MEMBER2, GROUP_MEMBERS_BILLS_DEBTORS)
        val res3 = memberGetsFromGroupTotal(MEMBER3, GROUP_MEMBERS_BILLS_DEBTORS)

        Assert.assertEquals(
                "member 1 gets total",
                BigDecimal(7.939).setScale(2, RoundingMode.UP),
                res1.setScale(2, RoundingMode.UP))
        Assert.assertEquals(
                "member 2 gets total",
                BigDecimal(22.2).setScale(2, RoundingMode.UP),
                res2.setScale(2, RoundingMode.UP))
        Assert.assertEquals(
                "member 3 gets total",
                BigDecimal.ZERO.setScale(2, RoundingMode.UP),
                res3.setScale(2, RoundingMode.UP))
    }

    @Test
    fun testMemberOwesGroup_AllMembersInGroup() {
        val res1 = memberOwesGroup(MEMBER1, GROUP_MEMBERS_BILLS_DEBTORS)
        val res2 = memberOwesGroup(MEMBER2, GROUP_MEMBERS_BILLS_DEBTORS)
        val res3 = memberOwesGroup(MEMBER3, GROUP_MEMBERS_BILLS_DEBTORS)

        Assert.assertEquals("Balance map member 1 size", 1, res1.size)
        Assert.assertEquals("Balance map member 2 size", 1, res2.size)
        Assert.assertEquals("Balance map member 3 size", 2, res3.size)

        Assert.assertEquals("member 2 in balance map of member 1", true, res1.containsKey(MEMBER2.email))

        Assert.assertEquals("member 1 in balance map of member 2", true, res2.containsKey(MEMBER1.email))

        Assert.assertEquals("member 1 in balance map of member 3", true, res3.containsKey(MEMBER1.email))
        Assert.assertEquals("member 2 in balance map of member 3", true, res3.containsKey(MEMBER2.email))

        Assert.assertEquals(
                "member 1 owes member 2",
                BigDecimal(11.1).setScale(2, RoundingMode.UP),
                res1[MEMBER2.email]?.setScale(2, RoundingMode.UP))

        Assert.assertEquals(
                "member 2 owes member 1",
                BigDecimal(6.47).setScale(2, RoundingMode.UP),
                res2[MEMBER1.email]?.setScale(2, RoundingMode.UP))

        Assert.assertEquals(
                "member 3 owes member 1",
                BigDecimal(1.47).setScale(2, RoundingMode.UP),
                res3[MEMBER1.email]?.setScale(2, RoundingMode.UP))
        Assert.assertEquals(
                "member 3 owes member 2",
                BigDecimal(11.1).setScale(2, RoundingMode.UP),
                res3[MEMBER2.email]?.setScale(2, RoundingMode.UP))
    }

    @Test
    fun testMemberOwesGroup_MemberNotInGroup() {
        val memberNotInGroup = Member("name", "email")
        val res = memberOwesGroup(memberNotInGroup, GROUP_MEMBERS_BILLS_DEBTORS)

        Assert.assertEquals("no members in balance map", 0, res.size)
    }

    @Test
    fun testMemberOwesGroupTotal() {
        val res1 = memberOwesGroupTotal(MEMBER1, GROUP_MEMBERS_BILLS_DEBTORS)
        val res2 = memberOwesGroupTotal(MEMBER2, GROUP_MEMBERS_BILLS_DEBTORS)
        val res3 = memberOwesGroupTotal(MEMBER3, GROUP_MEMBERS_BILLS_DEBTORS)

        Assert.assertEquals(
                "member 1 gets total",
                BigDecimal(11.1).setScale(2, RoundingMode.UP),
                res1.setScale(2, RoundingMode.UP))
        Assert.assertEquals(
                "member 2 gets total",
                BigDecimal(6.47).setScale(2, RoundingMode.UP),
                res2.setScale(2, RoundingMode.UP))
        Assert.assertEquals(
                "member 3 gets total",
                BigDecimal(12.56).setScale(2, RoundingMode.UP),
                res3.setScale(2, RoundingMode.UP))
    }

    @Test
    fun testOweGetMapMerge() {
        val get = memberGetsFromGroup(MEMBER1, GROUP_MEMBERS_BILLS_DEBTORS)
        val owe = memberOwesGroup(MEMBER1, GROUP_MEMBERS_BILLS_DEBTORS)
        val res =  oweGetMapMerge(owe, get)

        Assert.assertEquals("Get balance map member 1 size", 1, res.second.size)
        Assert.assertEquals("Owe balance map member 1 size", 1, res.first.size)

        Assert.assertEquals("member 1 gets from member 3",
                BigDecimal(1.47).setScale(2, RoundingMode.UP),
                res.second[MEMBER3.email]?.setScale(2, RoundingMode.UP))

        Assert.assertEquals("member 1 owes from member 2",
                BigDecimal(4.64).setScale(2, RoundingMode.UP),
                res.first[MEMBER2.email]?.setScale(2, RoundingMode.UP))
    }

    @Test
    fun testOweGetMapMerge_MembersOweGetSame() {
        val mem1 = Member("mem1", "mail1")
        val mem2 = Member("mem2", "mail2")

        val grpMem1 = GroupMember("group", "mail1")
        val grpMem2 = GroupMember("group", "mail2")

        val bill1 = Bill(Date(), "bill1", BigDecimal(12), "EUR", "mail1", "group", true)
        val bill1Debtor1 = Debtor(bill1.id, "mail1", BigDecimal(6))
        val bill1Debtor2 = Debtor(bill1.id, "mail2", BigDecimal(6))

        val bill2 = Bill(Date(), "bill2", BigDecimal(12), "EUR", "mail2", "group", true)
        val bill2Debtor1 = Debtor(bill2.id, "mail1", BigDecimal(6))
        val bill2Debtor2 = Debtor(bill2.id, "mail2", BigDecimal(6))

        val bill1Debtors =  BillDebtors()
        bill1Debtors.bill = bill1
        bill1Debtors.debtors = listOf(bill1Debtor1, bill1Debtor2)

        val bill2Debtors =  BillDebtors()
        bill2Debtors.bill = bill2
        bill2Debtors.debtors = listOf(bill2Debtor1, bill2Debtor2)

        val groupMembersBillsDebtors = GroupMembersBillsDebtors()
        groupMembersBillsDebtors.group = Group("group")
        groupMembersBillsDebtors.bills = listOf(bill1Debtors, bill2Debtors)
        groupMembersBillsDebtors.members = listOf(grpMem1, grpMem2)


        val get1 = memberGetsFromGroup(mem1, groupMembersBillsDebtors)
        val owe1 = memberOwesGroup(mem1, groupMembersBillsDebtors)
        val res1 =  oweGetMapMerge(owe1, get1)

        val get2 = memberGetsFromGroup(mem2, groupMembersBillsDebtors)
        val owe2 = memberOwesGroup(mem2, groupMembersBillsDebtors)
        val res2 =  oweGetMapMerge(owe2, get2)

        Assert.assertEquals(BigDecimal(6), get1["mail2"])
        Assert.assertEquals(BigDecimal(6), owe1["mail2"])

        Assert.assertEquals(BigDecimal(6), get2["mail1"])
        Assert.assertEquals(BigDecimal(6), owe2["mail1"])

        Assert.assertEquals(0, res1.second.size)
        Assert.assertEquals(0, res1.first.size)

        Assert.assertEquals(0, res2.second.size)
        Assert.assertEquals(0, res2.first.size)
    }

    companion object {
        private const val CURRENCY = "EUR"
        private const val GROUP_NAME = "GROUP_MEMBERS_BILLS_DEBTORS"

        private const val MEMBER_NAME1 = "name1"
        private const val MEMBER_EMAIL1 = "email1"
        private const val MEMBER_NAME2 = "name2"
        private const val MEMBER_EMAIL2 = "email2"
        private const val MEMBER_NAME3 = "name3"
        private const val MEMBER_EMAIL3 = "email3"

        private val GROUP = Group(GROUP_NAME)

        private val MEMBER1 = Member(MEMBER_NAME1, MEMBER_EMAIL1)
        private val MEMBER2= Member(MEMBER_NAME2, MEMBER_EMAIL2)
        private val MEMBER3= Member(MEMBER_NAME3, MEMBER_EMAIL3)

        private val GROUP_MEMBER1 = GroupMember(GROUP.name, MEMBER1.email)
        private val GROUP_MEMBER2 = GroupMember(GROUP.name, MEMBER2.email)
        private val GROUP_MEMBER3 = GroupMember(GROUP.name, MEMBER3.email)

        private val BILL1 = Bill(Date(), "desc1", BigDecimal(4.4), CURRENCY, MEMBER1.email, GROUP.name, true)
        private val BILL2 = Bill(Date(), "desc2", BigDecimal(22.2), CURRENCY, MEMBER2.email, GROUP.name, true)
        private val BILL3 = Bill(Date(), "desc3", BigDecimal(10), CURRENCY, MEMBER1.email, GROUP.name, true)
        private val BILL4 = Bill(Date(), "desc4", BigDecimal(5), CURRENCY, MEMBER3.email, GROUP.name, false)

        private val BILL1_DEBTOR1 = Debtor(BILL1.id, MEMBER1.email, BILL1.amount.divide(BigDecimal(3), MATH_CONTEXT))
        private val BILL1_DEBTOR2 = Debtor(BILL1.id, MEMBER2.email, BILL1.amount.divide(BigDecimal(3), MATH_CONTEXT))
        private val BILL1_DEBTOR3 = Debtor(BILL1.id, MEMBER3.email, BILL1.amount.divide(BigDecimal(3), MATH_CONTEXT))

        private val BILL2_DEBTOR1 = Debtor(BILL2.id, MEMBER1.email, BILL2.amount.divide(BigDecimal(2), MATH_CONTEXT))
        private val BILL2_DEBTOR2 = Debtor(BILL2.id, MEMBER3.email, BILL2.amount.divide(BigDecimal(2), MATH_CONTEXT))

        private val BILL3_DEBTOR1 = Debtor(BILL3.id, MEMBER1.email, BILL3.amount.divide(BigDecimal(2), MATH_CONTEXT))
        private val BILL3_DEBTOR2 = Debtor(BILL3.id, MEMBER2.email, BILL3.amount.divide(BigDecimal(2), MATH_CONTEXT))

        private val BILL4_DEBTOR1 = Debtor(BILL4.id, MEMBER1.email, BILL4.amount.divide(BigDecimal(3), MATH_CONTEXT))
        private val BILL4_DEBTOR2 = Debtor(BILL4.id, MEMBER2.email, BILL4.amount.divide(BigDecimal(3), MATH_CONTEXT))
        private val BILL4_DEBTOR3 = Debtor(BILL4.id, MEMBER3.email, BILL4.amount.divide(BigDecimal(3), MATH_CONTEXT))

        private val BILL1_DEBTORS = BillDebtors().apply {
            debtors = listOf(BILL1_DEBTOR1, BILL1_DEBTOR2, BILL1_DEBTOR3)
            bill = BILL1
        }
        private val BILL2_DEBTORS = BillDebtors().apply {
            debtors = listOf(BILL2_DEBTOR1, BILL2_DEBTOR2)
            bill = BILL2
        }
        private val BILL3_DEBTORS = BillDebtors().apply {
            debtors = listOf(BILL3_DEBTOR1, BILL3_DEBTOR2)
            bill = BILL3
        }
        private val BILL4_DEBTORS = BillDebtors().apply {
            debtors = listOf(BILL4_DEBTOR1, BILL4_DEBTOR2, BILL4_DEBTOR3)
            bill = BILL4
        }

        private val GROUP_MEMBERS_BILLS_DEBTORS = GroupMembersBillsDebtors().apply {
            group = GROUP
            bills = listOf(BILL1_DEBTORS, BILL2_DEBTORS, BILL3_DEBTORS, BILL4_DEBTORS)
            members = listOf(GROUP_MEMBER1, GROUP_MEMBER2, GROUP_MEMBER3)
        }
    }
}