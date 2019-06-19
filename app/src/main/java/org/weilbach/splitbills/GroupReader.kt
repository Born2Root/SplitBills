package org.weilbach.splitbills

import android.util.Xml
import org.weilbach.splitbills.data.*
import org.weilbach.splitbills.data.local.Converter
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.math.BigDecimal
import java.util.*


private val ns: String? = null
private const val XML_TAG_GROUP = "group"
private const val XML_TAG_NAME = "name"
private const val XML_TAG_GROUP_NAME = "groupName"
private const val XML_TAG_MEMBERS = "members"
private const val XML_TAG_MEMBER = "member"
private const val XML_TAG_EMAIL = "email"
private const val XML_TAG_BILLS = "bills"
private const val XML_TAG_BILL = "bill"
private const val XML_TAG_DATE_TIME = "dateTime"
private const val XML_TAG_AMOUNT = "amount"
private const val XML_TAG_CREDITOR_EMAIL = "creditorEmail"
private const val XML_TAG_DEBTORS = "debtors"
private const val XML_TAG_DEBTOR = "debtor"
private const val XML_TAG_DESCRIPTION = "description"
private const val XML_TAG_VALID = "valid"
private const val XML_TAG_BILL_ID = "billId"
private const val XML_TAG_MEMBER_EMAIL = "memberEmail"
private const val XML_TAG_CURRENCY = "currency"
private const val XML_TAG_DB_VERSION = "dbVersion"

private val converter = Converter()

@Throws(XmlPullParserException::class, IOException::class)
fun importGroupFromXml(inputStream: InputStream): Triple<GroupMembersBillsDebtors, List<Member>, String> {
    return parseXml(inputStream)
}

@Throws(XmlPullParserException::class, IOException::class)
private fun parseXml(inputStream: InputStream): Triple<GroupMembersBillsDebtors, List<Member>, String> {
    val parser = Xml.newPullParser()
    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
    parser.setInput(inputStream, null)
    parser.nextTag()
    return readGroup(parser)
}

@Throws(XmlPullParserException::class, IOException::class)
private fun readGroup(parser: XmlPullParser): Triple<GroupMembersBillsDebtors, List<Member>, String> {
    var dbVersion = ""
    var groupName = ""
    var bills = emptyList<BillDebtors>()
    var members = emptyList<Member>()

    parser.require(XmlPullParser.START_TAG, ns, XML_TAG_GROUP)
    while (parser.next() != XmlPullParser.END_TAG) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            continue
        }
        when (parser.name) {
            XML_TAG_DB_VERSION -> {
                dbVersion = readDbVersion(parser)
            }
            XML_TAG_NAME -> {
                groupName = readName(parser)
            }
            XML_TAG_MEMBERS -> {
                members = readMembers(parser)
            }
            XML_TAG_BILLS -> {
                bills = readBills(parser)
            }
            else -> {
                skip(parser)
            }
        }
    }
    val groupMembers = members.map { member -> GroupMember(groupName, member.email) }

    val groupMembersBillsDebtors = GroupMembersBillsDebtors()
    groupMembersBillsDebtors.group = Group(groupName)
    groupMembersBillsDebtors.members = groupMembers
    groupMembersBillsDebtors.bills = bills

    return Triple(groupMembersBillsDebtors, members, dbVersion)
}

@Throws(XmlPullParserException::class, IOException::class)
private fun readBills(parser: XmlPullParser): List<BillDebtors> {
    val bills = LinkedList<BillDebtors>()

    parser.require(XmlPullParser.START_TAG, null, XML_TAG_BILLS)
    while (parser.next() != XmlPullParser.END_TAG) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            continue
        }
        when (parser.name) {
            XML_TAG_BILL -> {
                bills.add(readBill(parser))
            }
            else -> {
                skip(parser)
            }
        }
    }
    return bills
}

@Throws(XmlPullParserException::class, IOException::class)
private fun readBill(parser: XmlPullParser): BillDebtors {
    var debtors = emptyList<Debtor>()
    var dateTime = Date()
    var description = ""
    var amount = BigDecimal.ZERO
    var currency = ""
    var creditorEmail = ""
    var valid = false
    var groupName = ""

    parser.require(XmlPullParser.START_TAG, null, XML_TAG_BILL)
    while (parser.next() != XmlPullParser.END_TAG) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            continue
        }
        when (parser.name) {
            XML_TAG_GROUP_NAME -> {
                groupName = readGroupName(parser)
            }
            XML_TAG_DATE_TIME -> {
                dateTime = readDateTime(parser)
            }
            XML_TAG_DESCRIPTION -> {
                description = readDescription(parser)
            }
            XML_TAG_CREDITOR_EMAIL -> {
                creditorEmail = readCreditorEmail(parser)
            }
            XML_TAG_VALID -> {
                valid = readValid(parser)
            }
            XML_TAG_DEBTORS -> {
                debtors = readDebtors(parser)
            }
            XML_TAG_AMOUNT -> {
                amount = readAmount(parser)
            }
            XML_TAG_CURRENCY -> {
                currency = readCurrency(parser)
            }
            else -> {
                skip(parser)
            }
        }
    }

    val billDebtors = BillDebtors()
    billDebtors.bill = Bill(dateTime, description, amount, currency, creditorEmail, groupName, valid)
    billDebtors.debtors = debtors

    return billDebtors
}

@Throws(XmlPullParserException::class, IOException::class)
private fun readValid(parser: XmlPullParser): Boolean {
    parser.require(XmlPullParser.START_TAG, null, XML_TAG_VALID)
    val valid = java.lang.Boolean.parseBoolean(readText(parser))
    parser.require(XmlPullParser.END_TAG, null, XML_TAG_VALID)
    return valid
}

@Throws(XmlPullParserException::class, IOException::class)
private fun readDebtors(parser: XmlPullParser): List<Debtor> {
    val debtors = LinkedList<Debtor>()

    parser.require(XmlPullParser.START_TAG, null, XML_TAG_DEBTORS)
    while (parser.next() != XmlPullParser.END_TAG) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            continue
        }
        when (parser.name) {
            XML_TAG_DEBTOR -> {
                debtors.add(readDebtor(parser))
            }
            else -> {
                skip(parser)
            }
        }
    }
    return debtors
}

@Throws(XmlPullParserException::class, IOException::class)
private fun readDebtor(parser: XmlPullParser): Debtor {
    var memberEmail = ""
    var billId = ""
    var amount = BigDecimal.ZERO

    parser.require(XmlPullParser.START_TAG, null, XML_TAG_DEBTOR)
    while (parser.next() != XmlPullParser.END_TAG) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            continue
        }
        when (parser.name) {
            XML_TAG_MEMBER_EMAIL -> {
                memberEmail = readMemberEmail(parser)
            }
            XML_TAG_BILL_ID -> {
                billId = readBillId(parser)
            }
            XML_TAG_AMOUNT -> {
                amount = readAmount(parser)
            }
            else -> {
                skip(parser)
            }
        }
    }
    return Debtor(billId, memberEmail, amount)
}

@Throws(XmlPullParserException::class, IOException::class)
private fun readMembers(parser: XmlPullParser): List<Member> {
    val members = LinkedList<Member>()

    parser.require(XmlPullParser.START_TAG, null, XML_TAG_MEMBERS)
    while (parser.next() != XmlPullParser.END_TAG) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            continue
        }
        when (parser.name) {
            XML_TAG_MEMBER -> {
                members.add(readMember(parser))
            }
            else -> {
                skip(parser)
            }
        }
    }
    return members
}

@Throws(XmlPullParserException::class, IOException::class)
private fun readMember(parser: XmlPullParser): Member {
    var email = ""
    var name = ""

    parser.require(XmlPullParser.START_TAG, null, XML_TAG_MEMBER)
    while (parser.next() != XmlPullParser.END_TAG) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            continue
        }
        when (parser.name) {
            XML_TAG_EMAIL -> {
                email = readEmail(parser)
            }
            XML_TAG_NAME -> {
                name = readName(parser)
            }
            else -> {
                skip(parser)
            }
        }
    }
    return Member(name, email)
}

@Throws(XmlPullParserException::class, IOException::class)
private fun readEmail(parser: XmlPullParser): String {
    parser.require(XmlPullParser.START_TAG, null, XML_TAG_EMAIL)
    val email = readText(parser)
    parser.require(XmlPullParser.END_TAG, null, XML_TAG_EMAIL)
    return email
}

@Throws(XmlPullParserException::class, IOException::class)
private fun readCurrency(parser: XmlPullParser): String {
    parser.require(XmlPullParser.START_TAG, null, XML_TAG_CURRENCY)
    val currency = readText(parser)
    parser.require(XmlPullParser.END_TAG, null, XML_TAG_CURRENCY)
    return currency
}

@Throws(XmlPullParserException::class, IOException::class)
private fun readCreditorEmail(parser: XmlPullParser): String {
    parser.require(XmlPullParser.START_TAG, null, XML_TAG_CREDITOR_EMAIL)
    val email = readText(parser)
    parser.require(XmlPullParser.END_TAG, null, XML_TAG_CREDITOR_EMAIL)
    return email
}

@Throws(XmlPullParserException::class, IOException::class)
private fun readMemberEmail(parser: XmlPullParser): String {
    parser.require(XmlPullParser.START_TAG, null, XML_TAG_MEMBER_EMAIL)
    val email = readText(parser)
    parser.require(XmlPullParser.END_TAG, null, XML_TAG_MEMBER_EMAIL)
    return email
}

@Throws(XmlPullParserException::class, IOException::class)
private fun readBillId(parser: XmlPullParser): String {
    parser.require(XmlPullParser.START_TAG, null, XML_TAG_BILL_ID)
    val billId = readText(parser)
    parser.require(XmlPullParser.END_TAG, null, XML_TAG_BILL_ID)
    return billId
}

@Throws(XmlPullParserException::class, IOException::class)
private fun readName(parser: XmlPullParser): String {
    parser.require(XmlPullParser.START_TAG, null, XML_TAG_NAME)
    val name = readText(parser)
    parser.require(XmlPullParser.END_TAG, null, XML_TAG_NAME)
    return name
}

@Throws(XmlPullParserException::class, IOException::class)
private fun readGroupName(parser: XmlPullParser): String {
    parser.require(XmlPullParser.START_TAG, null, XML_TAG_GROUP_NAME)
    val name = readText(parser)
    parser.require(XmlPullParser.END_TAG, null, XML_TAG_GROUP_NAME)
    return name
}

@Throws(XmlPullParserException::class, IOException::class)
private fun readDescription(parser: XmlPullParser): String {
    parser.require(XmlPullParser.START_TAG, null, XML_TAG_DESCRIPTION)
    val name = readText(parser)
    parser.require(XmlPullParser.END_TAG, null, XML_TAG_DESCRIPTION)
    return name
}

@Throws(XmlPullParserException::class, IOException::class)
private fun readDateTime(parser: XmlPullParser): Date {
    parser.require(XmlPullParser.START_TAG, null, XML_TAG_DATE_TIME)
    val timestamp = readText(parser)
    parser.require(XmlPullParser.END_TAG, null, XML_TAG_DATE_TIME)
    return Date(timestamp.toLong())
}

@Throws(XmlPullParserException::class, IOException::class)
private fun readAmount(parser: XmlPullParser): BigDecimal {
    parser.require(XmlPullParser.START_TAG, null, XML_TAG_AMOUNT)
    val amount = converter.stringToBigDecimal(readText(parser))
    parser.require(XmlPullParser.END_TAG, null, XML_TAG_AMOUNT)
    return amount
}

@Throws(XmlPullParserException::class, IOException::class)
private fun readDbVersion(parser: XmlPullParser): String {
    parser.require(XmlPullParser.START_TAG, null, XML_TAG_DB_VERSION)
    val version = readText(parser)
    parser.require(XmlPullParser.END_TAG, null, XML_TAG_DB_VERSION)
    return version
}

@Throws(IOException::class, XmlPullParserException::class)
private fun readText(parser: XmlPullParser): String {
    var result = ""
    if (parser.next() == XmlPullParser.TEXT) {
        result = parser.text
        parser.nextTag()
    }
    return result
}

@Throws(XmlPullParserException::class, IOException::class)
private fun skip(parser: XmlPullParser) {
    if (parser.eventType != XmlPullParser.START_TAG) {
        throw IllegalStateException()
    }
    var depth = 1
    while (depth != 0) {
        when (parser.next()) {
            XmlPullParser.END_TAG -> depth--
            XmlPullParser.START_TAG -> depth++
        }
    }
}
