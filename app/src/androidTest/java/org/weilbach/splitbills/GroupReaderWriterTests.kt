package org.weilbach.splitbills

import androidx.test.runner.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.weilbach.splitbills.data.*
import java.io.ByteArrayInputStream
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.collections.ArrayList


@RunWith(AndroidJUnit4::class)
class GroupReaderWriterTests {

    @Test
    fun testWriteGroupToXml() {
        val members = ArrayList<Member>().apply {
            add(MEMBER1)
            add(MEMBER2)
            add(MEMBER3)
        }
        val res = writeGroupToXml(GROUP_MEMBERS_BILLS_DEBTORS, members)
        Assert.assertEquals("valid xml", VALID_XML, res)
    }

    @Test
    fun testReadGroupFromXml() {
        // Tests just if everything is there, does not test if xml file
        // contains to much information
        val inputStream = VALID_XML.byteInputStream()
        val res = readGroupFromXml(inputStream)

        Assert.assertEquals("Db version matches", "1", res.third)

        ArrayList<Member>().apply {
            add(MEMBER1)
            add(MEMBER2)
            add(MEMBER3)
        }.forEach { member ->
            Assert.assertEquals("Member contained", true, res.second.contains(member))
        }

        Assert.assertEquals("Group name matches", GROUP_MEMBERS_BILLS_DEBTORS.group.name, res.first.group.name)

        GROUP_MEMBERS_BILLS_DEBTORS.members.forEach { member ->
            Assert.assertEquals(member.groupName, res.first.group.name)
            Assert.assertEquals(true, res.first.members.contains(member))
        }

        GROUP_MEMBERS_BILLS_DEBTORS.bills.forEach { bill ->
            Assert.assertEquals(true, res.first.bills.contains(bill))
            val readBill = res.first.bills[res.first.bills.indexOf(bill)]
            bill.debtors.forEach { debtor ->
                Assert.assertEquals(true, readBill.debtors.contains(debtor))
            }
        }
    }

    companion object {
        private const val CURRENCY = "EUR"
        private const val GROUP_NAME = "group name"

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

        private val BILL1 = Bill(Date(1000), "desc1", BigDecimal("9.90"), CURRENCY, MEMBER1.email, GROUP.name, true)
        private val BILL2 = Bill(Date(1001), "desc2", BigDecimal("22.00"), CURRENCY, MEMBER2.email, GROUP.name, true)
        private val BILL3 = Bill(Date(1002), "desc3", BigDecimal("10.00"), CURRENCY, MEMBER1.email, GROUP.name, true)
        private val BILL4 = Bill(Date(1003), "desc4", BigDecimal("15.00"), CURRENCY, MEMBER3.email, GROUP.name, false)

        private val BILL1_DEBTOR1 = Debtor(BILL1.id, MEMBER1.email, BigDecimal("3.30"))
        private val BILL1_DEBTOR2 = Debtor(BILL1.id, MEMBER2.email, BigDecimal("3.30"))
        private val BILL1_DEBTOR3 = Debtor(BILL1.id, MEMBER3.email, BigDecimal("3.30"))

        private val BILL2_DEBTOR1 = Debtor(BILL2.id, MEMBER1.email, BigDecimal("11.00"))
        private val BILL2_DEBTOR2 = Debtor(BILL2.id, MEMBER3.email, BigDecimal("11.00"))

        private val BILL3_DEBTOR1 = Debtor(BILL3.id, MEMBER1.email, BigDecimal("5.00"))
        private val BILL3_DEBTOR2 = Debtor(BILL3.id, MEMBER2.email, BigDecimal("5.00"))

        private val BILL4_DEBTOR1 = Debtor(BILL4.id, MEMBER1.email, BigDecimal("5.00"))
        private val BILL4_DEBTOR2 = Debtor(BILL4.id, MEMBER2.email, BigDecimal("5.00"))
        private val BILL4_DEBTOR3 = Debtor(BILL4.id, MEMBER3.email, BigDecimal("5.00"))

        private val BILL1_DEBTORS = BillDebtors().apply {
            val list = ArrayList<Debtor>().apply {
                add(BILL1_DEBTOR1)
                add(BILL1_DEBTOR2)
                add(BILL1_DEBTOR3)
            }
            debtors = list
            bill = BILL1
        }
        private val BILL2_DEBTORS = BillDebtors().apply {
            debtors = ArrayList<Debtor>().apply {
                add(BILL2_DEBTOR1)
                add(BILL2_DEBTOR2)
            }
            bill = BILL2
        }
        private val BILL3_DEBTORS = BillDebtors().apply {
            debtors = ArrayList<Debtor>().apply {
                add(BILL3_DEBTOR1)
                add(BILL3_DEBTOR2)
            }
            bill = BILL3
        }
        private val BILL4_DEBTORS = BillDebtors().apply {
            debtors = ArrayList<Debtor>().apply {
                add(BILL4_DEBTOR1)
                add(BILL4_DEBTOR2)
                add(BILL4_DEBTOR3)
            }
            bill = BILL4
        }

        private val GROUP_MEMBERS_BILLS_DEBTORS = GroupMembersBillsDebtors().apply {
            group = GROUP
            bills = ArrayList<BillDebtors>().apply {
                add(BILL1_DEBTORS)
                add(BILL2_DEBTORS)
                add(BILL3_DEBTORS)
                add(BILL4_DEBTORS)
            }
            members = ArrayList<GroupMember>().apply {
                add(GROUP_MEMBER1)
                add(GROUP_MEMBER2)
                add(GROUP_MEMBER3)
            }
        }

        private const val VALID_XML = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?><group><dbVersion>1</dbVersion><name>group name</name><members><member><name>name1</name><email>email1</email></member><member><name>name2</name><email>email2</email></member><member><name>name3</name><email>email3</email></member></members><bills><bill><dateTime>1000</dateTime><groupName>group name</groupName><description>desc1</description><amount>9.90</amount><currency>EUR</currency><creditorEmail>email1</creditorEmail><valid>true</valid><debtors><debtor><billId>1000desc1group name</billId><memberEmail>email1</memberEmail><amount>3.30</amount></debtor><debtor><billId>1000desc1group name</billId><memberEmail>email2</memberEmail><amount>3.30</amount></debtor><debtor><billId>1000desc1group name</billId><memberEmail>email3</memberEmail><amount>3.30</amount></debtor></debtors></bill><bill><dateTime>1001</dateTime><groupName>group name</groupName><description>desc2</description><amount>22.00</amount><currency>EUR</currency><creditorEmail>email2</creditorEmail><valid>true</valid><debtors><debtor><billId>1001desc2group name</billId><memberEmail>email1</memberEmail><amount>11.00</amount></debtor><debtor><billId>1001desc2group name</billId><memberEmail>email3</memberEmail><amount>11.00</amount></debtor></debtors></bill><bill><dateTime>1002</dateTime><groupName>group name</groupName><description>desc3</description><amount>10.00</amount><currency>EUR</currency><creditorEmail>email1</creditorEmail><valid>true</valid><debtors><debtor><billId>1002desc3group name</billId><memberEmail>email1</memberEmail><amount>5.00</amount></debtor><debtor><billId>1002desc3group name</billId><memberEmail>email2</memberEmail><amount>5.00</amount></debtor></debtors></bill><bill><dateTime>1003</dateTime><groupName>group name</groupName><description>desc4</description><amount>15.00</amount><currency>EUR</currency><creditorEmail>email3</creditorEmail><valid>false</valid><debtors><debtor><billId>1003desc4group name</billId><memberEmail>email1</memberEmail><amount>5.00</amount></debtor><debtor><billId>1003desc4group name</billId><memberEmail>email2</memberEmail><amount>5.00</amount></debtor><debtor><billId>1003desc4group name</billId><memberEmail>email3</memberEmail><amount>5.00</amount></debtor></debtors></bill></bills></group>"
    }
}
