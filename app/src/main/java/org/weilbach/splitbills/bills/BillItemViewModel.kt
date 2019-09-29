package org.weilbach.splitbills.bills

import android.content.Context
import android.text.Spanned
import androidx.lifecycle.*
import org.weilbach.splitbills.R
import org.weilbach.splitbills.data.Bill
import org.weilbach.splitbills.data.Debtor
import org.weilbach.splitbills.data.Group
import org.weilbach.splitbills.prettyPrintNum
import org.weilbach.splitbills.util.AppExecutors
import org.weilbach.splitbills.util.fromHtml
import org.weilbach.splitbills.util.getUser
import java.text.SimpleDateFormat
import java.util.*

class BillItemViewModel(
        val bill: Bill,
        val group: Group,
        private val billsViewModel: BillsViewModel,
        private val appContext: Context
) : ViewModel() {

    private val membersRepository = billsViewModel.membersRepository
    private val debtorsRepository = billsViewModel.debtorsRepository

    val user = getUser(appContext)
    val currency = Currency.getInstance(bill.currency)

    val date = MutableLiveData<Spanned>().apply {
        // val d = Bill.formatter.parse(bill.dateTime)
        val formatted = SimpleDateFormat.getDateInstance().format(bill.dateTime)
        value = if (bill.valid) {
            fromHtml(formatted)
        } else {
            fromHtml("<strike>$formatted</strike>")
        }
    }

    private val debtors: LiveData<List<Debtor>> = debtorsRepository.getDebtorsByBillId(bill.id)

    private val _userLentOrBorrowedColor = MutableLiveData<Int>().apply {
        value = R.color.colorGet
    }
    val userLentOrBorrowedColor: LiveData<Int>
        get() = _userLentOrBorrowedColor

    val userLentOrBorrowed: LiveData<Spanned> = Transformations.map(debtors) { debtors ->
        if (debtors.isNotEmpty()) {
            val debtor = findDebtorByEmail(user.email, debtors)

            // TODO: Make this cleaner
            if (bill.creditorEmail == user.email) {
                // User is creditor
                if (debtor != null) {
                    _userLentOrBorrowedColor.value = R.color.colorGet
                    if (bill.valid) {
                        fromHtml(appContext.getString(R.string.you_lent, prettyPrintNum(debtor.amount), currency.symbol))
                    } else {
                        fromHtml("<strike>" + appContext.getString(R.string.you_lent, prettyPrintNum(debtor.amount), currency.symbol) + "</strike>")
                    }
                } else {
                    if (bill.valid) {
                        fromHtml(appContext.getString(R.string.you_lent, prettyPrintNum(bill.amount), currency.symbol))
                    } else {
                        fromHtml("<strike>" + appContext.getString(R.string.you_lent, prettyPrintNum(bill.amount), currency.symbol) + "</strike>")
                    }
                }
            } else {
                if (!debtors.contains(debtor)) {
                    if (bill.valid) {
                        fromHtml(appContext.getString(R.string.you_are_settled_up))
                    } else {
                        fromHtml("<strike>" + appContext.getString(R.string.you_are_settled_up) + "</strike>")
                    }
                } else {
                    _userLentOrBorrowedColor.value = R.color.colorOwe
                    if (debtor != null) {
                        if (bill.valid) {
                            fromHtml(appContext.getString(R.string.you_borrowed, prettyPrintNum(debtor.amount), currency.symbol))
                        } else {
                            fromHtml("<strike>" + appContext.getString(R.string.you_borrowed, prettyPrintNum(debtor.amount), currency.symbol) + "</strike>")
                        }
                    } else {
                        // This case should never happen
                        fromHtml(appContext.getString(R.string.error))
                    }
                }
            }
        } else {
            if (bill.valid) {
                fromHtml(appContext.getString(R.string.you_are_settled_up))
            } else {
                fromHtml("<strike>" + appContext.getString(R.string.you_are_settled_up) + "</strike>")
            }
        }
    }

    private val creditor = membersRepository.getMemberByEmail(bill.creditorEmail)

    val memberPaid: LiveData<Spanned> = Transformations.map(creditor) { member ->
        if (member.email == user.email) {
            _memberPaidColor.value = R.color.colorGet
        } else {
            _memberPaidColor.value = R.color.colorOwe
        }
        if (bill.valid) {
            fromHtml(appContext.getString(R.string.member_paid, member.name, bill.amount, currency.symbol))
        } else {
            fromHtml("<strike>" + appContext.getString(R.string.member_paid, member.name, bill.amount, currency.symbol) + "</strike>")
        }
    }

    private val _memberPaidColor = MutableLiveData<Int>().apply {
        value = R.color.colorGet
    }
    val memberPaidColor: LiveData<Int>
        get() = _memberPaidColor

    val description = MutableLiveData<Spanned>().apply {
        if (bill.valid) {
            value = fromHtml(bill.description)
            return@apply
        }
        value = fromHtml("<strike>${bill.description}</strike>")
    }

    private fun findDebtorByEmail(email: String, debtors: List<Debtor>): Debtor? {
        for (debtor in debtors) {
            if (debtor.memberEmail == email) {
                return debtor
            }
        }
        return null
    }

    companion object {
        private const val TAG = "BillItemViewModel"
    }
}