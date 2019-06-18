package org.weilbach.splitbills.billdetail

import android.content.Context
import android.text.Spanned
import androidx.lifecycle.*
import org.weilbach.splitbills.data.source.BillRepository
import org.weilbach.splitbills.prettyPrintNum
import org.weilbach.splitbills.R
import org.weilbach.splitbills.data.BillDebtors
import org.weilbach.splitbills.data.Debtor
import org.weilbach.splitbills.data.Member
import org.weilbach.splitbills.data.source.GroupMemberRepository
import org.weilbach.splitbills.data.source.MemberRepository
import org.weilbach.splitbills.util.fromHtml
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

class BillDetailViewModel(
        private val billRepository: BillRepository,
        private val memberRepository: MemberRepository,
        private val groupMemberRepository: GroupMemberRepository,
        private val appContext: Context
) : ViewModel() {

    private val _billId = MutableLiveData<String>()
    val billId: LiveData<String>
        get() = _billId

    val bill: LiveData<BillDebtors> = Transformations.switchMap(billId) { billId ->
        billRepository.getBillWithDebtorsById(billId)
    }

    val groupName: LiveData<String> = Transformations.map(bill) { bill ->
        bill.bill.groupName
    }

    val description: LiveData<String> = Transformations.map(bill) { bill ->
        bill.bill.description
    }

    val amount: LiveData<String> = Transformations.map(bill) { bill ->
        prettyPrintNum(bill.bill.amount) + " " + Currency.getInstance(bill.bill.currency).symbol
    }

    val creditor: LiveData<Member> = Transformations.switchMap(bill) { bill ->
        memberRepository.getMemberByEmail(bill.bill.creditorEmail)
    }

    val paidBy: LiveData<String> = MediatorLiveData<String>().apply {
        addSource(bill) { bill ->
            creditor.value?.let { creditor ->
                val formatted = SimpleDateFormat.getDateInstance().format(bill.bill.dateTime)
                value = appContext.getString(R.string.paid_by_on, creditor.name, formatted)
            }
        }

        addSource(creditor) { creditor ->
            bill.value?.let { bill ->
                val formatted = SimpleDateFormat.getDateInstance().format(bill.bill.dateTime)
                value = appContext.getString(R.string.paid_by_on, creditor.name, formatted)
            }
        }
    }

    val creditorPaid: LiveData<Spanned> = MediatorLiveData<Spanned>().apply {
        addSource(bill) { bill ->
            creditor.value?.let { creditor ->
                val currency = Currency.getInstance(bill.bill.currency).symbol
                value = fromHtml(
                        appContext.getString(
                                R.string.creditor_paid,
                                creditor.name,
                                prettyPrintNum(bill.bill.amount),
                                currency))
            }
        }

        addSource(creditor) { creditor ->
            bill.value?.let { bill ->
                val currency = Currency.getInstance(bill.bill.currency).symbol
                value = fromHtml(
                        appContext.getString(
                                R.string.creditor_paid,
                                creditor.name,
                                prettyPrintNum(bill.bill.amount),
                                currency))
            }
        }
    }

    val members: LiveData<List<Member>> = Transformations.switchMap(bill) { billDebtor ->
        groupMemberRepository.getGroupMembersMembersByGroupName(billDebtor.bill.groupName)
    }

    val membersOwe: LiveData<Spanned> = MediatorLiveData<Spanned>().apply {
        addSource(bill) { billDebtors ->
            members.value?.let { members ->
                val debtors = billDebtors.debtors
                var s = ""
                var i = 0
                members.forEach { member ->
                    if (i != 0) s += "<br>"
                    findAmountFromMember(member.email, debtors)?.let {
                        s += appContext.getString(
                                R.string.member_borrowed,
                                member.name,
                                prettyPrintNum(it),
                                billDebtors.bill.currency)
                        i++
                    }
                }
                value = fromHtml(s)
            }
        }

        addSource(members) { members ->
            bill.value?.let { billDebtors ->
                val debtors = billDebtors.debtors
                var s = ""
                var i = 0
                members.forEach { member ->
                    if (i != 0) s += "<br>"
                    findAmountFromMember(member.email, debtors)?.let {
                        s += appContext.getString(
                                R.string.member_borrowed,
                                member.name,
                                prettyPrintNum(it),
                                Currency.getInstance(billDebtors.bill.currency).symbol)
                        i++
                    }
                }
                value = fromHtml(s)
            }
        }
    }

    private fun findAmountFromMember(memberEmail: String, debtors: List<Debtor>): BigDecimal? {
        for (debtor in debtors) {
            if (memberEmail == debtor.memberEmail) {
                return debtor.amount
            }
        }
        return null
    }

    fun start(billId: String) {
        _billId.value = billId
    }
}