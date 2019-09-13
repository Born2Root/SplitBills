package org.weilbach.splitbills

import android.util.Xml
import org.weilbach.splitbills.data.GroupMembersBillsDebtors
import org.weilbach.splitbills.data.Member
import org.weilbach.splitbills.data.local.Converter
import java.io.StringWriter

private val converter = Converter()
private const val DB_VERSION = "1"

fun writeGroupToXml(group: GroupMembersBillsDebtors, members: List<Member>): String {
    val serializer = Xml.newSerializer()
    val writer = StringWriter()

    serializer.setOutput(writer)
    serializer.startDocument("UTF-8", true)

    serializer.startTag("", "group")

    serializer.startTag("", "dbVersion")
    serializer.text(DB_VERSION)
    serializer.endTag("", "dbVersion")

    serializer.startTag("", "name")
    serializer.text(group.group.name)
    serializer.endTag("", "name")

    serializer.startTag("", "members")
    members.forEach { member ->
        // TODO: Check member is really member of group

        serializer.startTag("", "member")
        serializer.startTag("", "name")
        serializer.text(member.name)
        serializer.endTag("", "name")
        serializer.startTag("", "email")
        serializer.text(member.email)
        serializer.endTag("", "email")
        serializer.endTag("", "member")
    }
    serializer.endTag("", "members")

    serializer.startTag("", "bills")
    group.bills.forEach { bill ->
        serializer.startTag("", "bill")

        serializer.startTag("", "dateTime")
        serializer.text(bill.bill.dateTime.time.toString())
        serializer.endTag("", "dateTime")

        serializer.startTag("", "groupName")
        serializer.text(bill.bill.groupName)
        serializer.endTag("", "groupName")

        serializer.startTag("", "description")
        serializer.text(bill.bill.description)
        serializer.endTag("", "description")

        serializer.startTag("", "amount")
        serializer.text(converter.bigDecimalToString(bill.bill.amount))
        serializer.endTag("", "amount")

        serializer.startTag("", "currency")
        serializer.text(bill.bill.currency)
        serializer.endTag("", "currency")

        serializer.startTag("", "creditorEmail")
        serializer.text(bill.bill.creditorEmail)
        serializer.endTag("", "creditorEmail")

        serializer.startTag("", "valid")
        serializer.text(bill.bill.valid.toString())
        serializer.endTag("", "valid")

        serializer.startTag("", "debtors")
        bill.debtors.forEach { debtor ->
            serializer.startTag("", "debtor")
            serializer.startTag("", "billId")
            serializer.text(debtor.billId)
            serializer.endTag("", "billId")
            serializer.startTag("", "memberEmail")
            serializer.text(debtor.memberEmail)
            serializer.endTag("", "memberEmail")
            serializer.startTag("", "amount")
            serializer.text(converter.bigDecimalToString(debtor.amount))
            serializer.endTag("", "amount")
            serializer.endTag("", "debtor")
        }
        serializer.endTag("", "debtors")

        serializer.endTag("", "bill")
    }
    serializer.endTag("", "bills")

    serializer.endTag("", "group")
    serializer.endDocument()
    return writer.toString()
}

/*fun writeGroupToXml(group: GroupData,
                    memberRepository: MembersRepository,
                    billRepository: BillsRepository,
                    groupMemberRepository: GroupsMembersRepository,
                    debtorRepository: DebtorsRepository,
                    amountsRepository: AmountsRepository,
                    appExecutors: AppExecutors,
                    groupWriterCallback: GroupWriterCallback
                    ) {
    appExecutors.diskIO.execute {
        val serializer = Xml.newSerializer()
        val writer = StringWriter()

        serializer.setOutput(writer)
        serializer.startDocument("UTF-8", true)
        serializer.startTag("", "group")
        serializer.startTag("", "name")
        serializer.text(group.name)
        serializer.endTag("", "name")

        serializer.startTag("", "members")
        val groupMembers = groupMemberRepository.getGroupMembersByGroupNameSync(group.name)
        groupMembers.forEach {
            memberRepository.getMemberSync(it.memberEmail)?.let { member ->
                serializer.startTag("", "member")
                serializer.startTag("", "name")
                serializer.text(member.name)
                serializer.endTag("", "name")
                serializer.startTag("", "email")
                serializer.text(member.email)
                serializer.endTag("", "email")
                serializer.endTag("", "member")
            }
        }
        serializer.endTag("", "members")

        serializer.startTag("", "bills")
        billRepository.getBillsByGroupNameSync(group.name).forEach { bill ->
            val debtors = debtorRepository.getDebtorsByBillIdSync(bill.id)
            val amounts = amountsRepository.getAmountsByBillIdSync(bill.id)
            serializer.startTag("", "bill")

            serializer.startTag("", "dateTime")
            serializer.text(bill.dateTime)
            serializer.endTag("", "dateTime")

            serializer.startTag("", "groupName")
            serializer.text(bill.groupName)
            serializer.endTag("", "groupName")

            serializer.startTag("", "description")
            serializer.text(bill.description)
            serializer.endTag("", "description")

            serializer.startTag("", "creditorEmail")
            serializer.text(bill.creditorEmail)
            serializer.endTag("", "creditorEmail")

            serializer.startTag("", "valid")
            serializer.text(bill.valid.toString())
            serializer.endTag("", "valid")

            serializer.startTag("", "debtors")
            debtors.forEach { debtor ->
                serializer.startTag("", "debtor")
                serializer.startTag("", "billId")
                serializer.text(debtor.billId)
                serializer.endTag("", "billId")
                serializer.startTag("", "memberEmail")
                serializer.text(debtor.memberEmail)
                serializer.endTag("", "memberEmail")
                serializer.endTag("", "debtor")
            }
            serializer.endTag("", "debtors")

            serializer.startTag("", "amounts")
            amounts.forEach { amount ->
                serializer.startTag("", "amount")
                serializer.startTag("", "amountInner")
                serializer.text(amount.amount)
                serializer.endTag("", "amountInner")
                serializer.startTag("", "billId")
                serializer.text(amount.billId)
                serializer.endTag("", "billId")
                serializer.startTag("", "currency")
                serializer.text(amount.currency)
                serializer.endTag("", "currency")
                serializer.startTag("", "valid")
                serializer.text(amount.valid.toString())
                serializer.endTag("", "valid")
                serializer.endTag("", "amount")
            }
            serializer.endTag("", "amounts")

            serializer.endTag("", "bill")
        }
        serializer.endTag("", "bills")

        serializer.endTag("", "group")
        serializer.endDocument()
        val xml = writer.toString()
        appExecutors.mainThread.execute {
            groupWriterCallback.onSuccess(xml)
        }
    }
}*/
