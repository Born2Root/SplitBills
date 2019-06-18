package org.weilbach.splitbills.bills

import android.content.Context
import android.text.Spanned
import androidx.lifecycle.*
import org.weilbach.splitbills.MATH_CONTEXT
import org.weilbach.splitbills.R
import org.weilbach.splitbills.data2.Bill
import org.weilbach.splitbills.data2.Debtor
import org.weilbach.splitbills.data2.Group
import org.weilbach.splitbills.prettyPrintNum
import org.weilbach.splitbills.util.AppExecutors
import org.weilbach.splitbills.util.fromHtml
import org.weilbach.splitbills.util.getUser
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

class BillItemViewModel(
        val bill: Bill,
        val group: Group,
        private val billsViewModel: BillsViewModel,
        private val appContext: Context,
        private val appExecutors: AppExecutors,
        private val lifecycleOwner: LifecycleOwner
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

    /*val userLentOrBorrowed = MutableLiveData<Spanned>().apply {
        value = if (bill.valid) {
            fromHtml(appContext.getString(R.string.you_are_settled_up))
            // appContext.getString(R.string.you_are_settled_up)
        } else {
            fromHtml("<strike>" + appContext.getString(R.string.you_are_settled_up) + "</strike>")
            //appContext.getString(R.string.you_are_settled_up)
        }
    }*/

    private val _userLentOrBorrowedColor = MutableLiveData<Int>().apply {
        value = R.color.colorGet
    }
    val userLentOrBorrowedColor: LiveData<Int>
        get() = _userLentOrBorrowedColor

    val userLentOrBorrowed: LiveData<Spanned> = Transformations.map(debtors) { debtors ->
        if (debtors.isNotEmpty()) {
            val debtor = findDebtorByEmail(user.email, debtors)
            // val res = bill.amount.divide(BigDecimal(debtors.size), MATH_CONTEXT)

            if (bill.creditorEmail == user.email) {
                if (debtor != null) {
                    _userLentOrBorrowedColor.value = R.color.colorGet
                    if (bill.valid) {
                        fromHtml(appContext.getString(R.string.you_lent, prettyPrintNum(debtor.amount), currency.symbol))
                    } else {
                        fromHtml("<strike>" + appContext.getString(R.string.you_lent, prettyPrintNum(debtor.amount), currency.symbol) + "</strike>")
                    }
                } else {
                    // This case should never happen
                    fromHtml(appContext.getString(R.string.error))
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

    /*val memberPaid = MutableLiveData<Spanned>().apply {
        // value = fromHtml("")
    }*/

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
            // value = fromHtml(bill.description)
            return@apply
        }
        value = fromHtml("<strike>${bill.description}</strike>")
        // value = fromHtml("<strike>${bill.description}</strike>")
    }

    init {
        setup()
    }

    private fun setup() {
        setMemberPaid()
        userLentOrBorrowed()
    }

    private fun setMemberPaid() {
        /*memberRepository.getMember(bill.creditorEmail, object : MembersDataSource.GetMemberCallback {
            override fun onMemberLoaded(memberData: MemberData) {
                amountsRepository.getValidAmountByBillId(bill.id, object : AmountsDataSource.GetAmountCallback {
                    override fun onAmountLoaded(amountData: AmountData) {
                        val user = getUser(appContext)
                        val currency = Currency.getInstance(amountData.currency)
                        if (memberData.email == user.email) {
                            _memberPaidColor.value = R.color.colorGet
                        } else {
                            _memberPaidColor.value = R.color.colorOwe
                        }
                        if (bill.valid) {
                            memberPaid.value = fromHtml(appContext.getString(R.string.member_paid, memberData.name, amountData.amount, currency.symbol))
                            // memberPaid.value = appContext.getString(R.string.member_paid, member.name, amount.amount, currency.symbol)
                        } else {
                            memberPaid.value = fromHtml("<strike>" + appContext.getString(R.string.member_paid, memberData.name, amountData.amount, currency.symbol) + "</strike>")
                            // memberPaid.value = appContext.getString(R.string.member_paid, member.name, amount.amount, currency.symbol)
                        }
                    }

                    override fun onDataNotAvailable() {
                        Log.w(TAG, "no amounts data available")
                    }
                })
            }

            override fun onDataNotAvailable() {
                Log.w(TAG, "no member data available")
            }
        })*/
    }

    private fun userLentOrBorrowed() {
        /*debtorRepository.getDebtorsByBillId(bill.id, object : DebtorsDataSource.GetDebtorsCallback {
            override fun onDebtorsLoaded(debtorData: List<DebtorData>) {

                amountsRepository.getValidAmountByBillId(bill.id, object : AmountsDataSource.GetAmountCallback {
                    override fun onAmountLoaded(amountData: AmountData) {
                        val user = getUser(appContext)
                        val currency = Currency.getInstance(amountData.currency)
                        val res = BigDecimal(amountData.amount).divide(BigDecimal(debtorData.size), MATH_CONTEXT)

                        if (bill.creditorEmail == user.email) {
                            _userLentOrBorrowedColor.value = R.color.colorGet
                            if (bill.valid) {
                                userLentOrBorrowed.value = fromHtml(appContext.getString(R.string.you_lent, prettyPrintNum(res), currency.symbol))
                                // userLentOrBorrowed.value = appContext.getString(R.string.you_lent, prettyPrintNum(res), currency.symbol)
                            } else {
                                userLentOrBorrowed.value = fromHtml("<strike>" + appContext.getString(R.string.you_lent, prettyPrintNum(res), currency.symbol) + "</strike>")
                                // userLentOrBorrowed.value = appContext.getString(R.string.you_lent, prettyPrintNum(res), currency.symbol)
                            }
                            return
                        }
                        if (!debtorData.contains(DebtorData(bill.id, user.email))) return
                        _userLentOrBorrowedColor.value = R.color.colorOwe
                        if (bill.valid) {
                            userLentOrBorrowed.value = fromHtml(appContext.getString(R.string.you_borrowed, prettyPrintNum(res), currency.symbol))
                            // userLentOrBorrowed.value = appContext.getString(R.string.you_borrowed, prettyPrintNum(res), currency.symbol)
                        } else {
                            userLentOrBorrowed.value = fromHtml("<strike>" + appContext.getString(R.string.you_borrowed, prettyPrintNum(res), currency.symbol) + "</strike>")
                            // userLentOrBorrowed.value = appContext.getString(R.string.you_borrowed, prettyPrintNum(res), currency.symbol)
                        }
                    }

                    override fun onDataNotAvailable() {
                        Log.w(TAG, "no amounts data available")
                    }
                })
            }

            override fun onDataNotAvailable() {
                Log.w(TAG, "no debtors data available")
            }
        })*/
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