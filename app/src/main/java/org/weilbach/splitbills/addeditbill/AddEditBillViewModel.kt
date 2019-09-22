package org.weilbach.splitbills.addeditbill

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.*
import org.weilbach.splitbills.MemberItemNavigator
import org.weilbach.splitbills.R
import org.weilbach.splitbills.data.Bill
import org.weilbach.splitbills.data.BillDebtors
import org.weilbach.splitbills.data.Group
import org.weilbach.splitbills.data.Member
import org.weilbach.splitbills.data.local.Converter
import org.weilbach.splitbills.data.source.*
import org.weilbach.splitbills.prettyPrintNum
import org.weilbach.splitbills.util.*
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList


class AddEditBillViewModel(
        private val groupsRepository: GroupRepository,
        private val billsRepository: BillRepository,
        private val debtorsRepository: DebtorRepository,
        private val membersRepository: MemberRepository,
        private val groupsMembersRepository: GroupMemberRepository,
        private val appExecutors: AppExecutors,
        val appContext: Context
) : ViewModel(), MemberItemNavigator, DebtorItemContainerListener {

    private val converter = Converter()

    private val _currencyItems = MutableLiveData<List<Currency>>().apply {
        value = Currency.getAvailableCurrencies().toList()
    }
    val currencyItems: LiveData<List<Currency>>
        get() = _currencyItems

    val posCurrencySpinner = MutableLiveData<Int>().apply {
        val currency = getCurrency(appContext)
        Currency.getAvailableCurrencies().indexOf(currency).let {
            value = it
        }
    }

    val currency: LiveData<Currency> = Transformations.map(posCurrencySpinner) { pos ->
        var currency = Currency.getInstance("EUR")

        currencyItems.value?.let { currencies ->
            if (pos < currencies.size) {
                currency = currencies[pos]
            }
        }
        debtorItemContainer.currency = currency
        currency
    }

    val currencySymbol: LiveData<String> = Transformations.map(currency) { currency ->
        currency.symbol
    }

    val description = MutableLiveData<String>()

    val amountToBalance = MutableLiveData<BigDecimal>()

    val percentageToBalance = MutableLiveData<BigDecimal>()

    val isAmountToBalance = Transformations.map(amountToBalance) { amountToBalance ->
        amountToBalance.compareTo(BigDecimal.ZERO) != 0
    }

    val isPercentageToBalance = Transformations.map(percentageToBalance) { percentageToBalance ->
        percentageToBalance.compareTo(BigDecimal.ZERO) != 0
    }

    private fun amountToBalancePretty(amountToBalance: BigDecimal,
                              percentageToBalance: BigDecimal,
                              isAmountToBalance: Boolean,
                              isPercentageToBalance: Boolean): Spanned {
        val currencySymbol = currency.value?.symbol ?: "NO CURRENCY SET"

        if (isAmountToBalance && isPercentageToBalance) {
            return fromHtml(appContext.getString(R.string.amount_to_balance_with_percentage_to_balance,
                    prettyPrintNum(amountToBalance), currencySymbol, prettyPrintNum(percentageToBalance)))
        }

        if (isAmountToBalance && !isPercentageToBalance) {
            return fromHtml(appContext.getString(R.string.amount_to_balance,
                    prettyPrintNum(amountToBalance), currencySymbol))
        }

        if (isPercentageToBalance && !isAmountToBalance) {
            return fromHtml(appContext.getString(R.string.percentage_to_balance, prettyPrintNum(percentageToBalance)))
        }

        return SpannableString(appContext.getString(R.string.nothing_to_balance))
    }

    val amountToBalancePretty = MediatorLiveData<Spanned>().apply {
        addSource(amountToBalance) { currentAmountToBalance ->
            val currentPercentageToBalance = percentageToBalance.value
            val currentIsAmountToBalance = isAmountToBalance.value
            val currentIsPercentageToBalance = isPercentageToBalance.value

            if (currentIsPercentageToBalance == null
                    || currentIsAmountToBalance == null
                    || currentAmountToBalance == null
                    || currentPercentageToBalance == null) {
                return@addSource
            }

            value = amountToBalancePretty(currentAmountToBalance,
                    currentPercentageToBalance, currentIsAmountToBalance, currentIsPercentageToBalance)
        }

        addSource(percentageToBalance) {currentPercentageToBalance ->
            val currentAmountToBalance = amountToBalance.value
            val currentIsAmountToBalance = isAmountToBalance.value
            val currentIsPercentageToBalance = isPercentageToBalance.value

            if (currentIsPercentageToBalance == null
                    || currentIsAmountToBalance == null
                    || currentAmountToBalance == null
                    || currentPercentageToBalance == null) {
                return@addSource
            }
            value = amountToBalancePretty(currentAmountToBalance,
                    currentPercentageToBalance, currentIsAmountToBalance, currentIsPercentageToBalance)
        }

        addSource(isAmountToBalance) { currentIsAmountToBalance ->
            val currentAmountToBalance = amountToBalance.value
            val currentPercentageToBalance = percentageToBalance.value
            val currentIsPercentageToBalance = isPercentageToBalance.value

            if (currentIsPercentageToBalance == null
                    || currentIsAmountToBalance == null
                    || currentAmountToBalance == null
                    || currentPercentageToBalance == null) {
                return@addSource
            }

            value = amountToBalancePretty(currentAmountToBalance,
                    currentPercentageToBalance, currentIsAmountToBalance, currentIsPercentageToBalance)
        }

        addSource(isPercentageToBalance) { currentIsPercentageToBalance ->
            val currentAmountToBalance = amountToBalance.value
            val currentPercentageToBalance = percentageToBalance.value
            val currentIsAmountToBalance = isAmountToBalance.value

            if (currentIsPercentageToBalance == null
                    || currentIsAmountToBalance == null
                    || currentAmountToBalance == null
                    || currentPercentageToBalance == null) {
                return@addSource
            }

            value = amountToBalancePretty(currentAmountToBalance,
                    currentPercentageToBalance, currentIsAmountToBalance, currentIsPercentageToBalance)
        }
    }

    private val _descriptionError = MutableLiveData<String>()
    val descriptionError: LiveData<String>
        get() = _descriptionError

    val amount = MutableLiveData<String>()

    private val _amountError = MutableLiveData<String>()
    val amountError: LiveData<String>
        get() = _amountError

    val posGroupSpinner = MutableLiveData<Int>()

    val group: LiveData<Group> = Transformations.map(posGroupSpinner) { pos ->
        var group = Group("")
        groupItems.value?.let { groups ->
            group = groups[pos]
        }
        group
    }

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarMessage: LiveData<Event<Int>>
        get() = _snackbarText

    private val _snackbarTextSpanned = MutableLiveData<Event<Spanned>>()
    val snackbarMessageSpanned: LiveData<Event<Spanned>>
        get() = _snackbarTextSpanned

    private val _billUpdated = MutableLiveData<Event<Bill>>()
    val billUpdatedEvent: LiveData<Event<Bill>>
        get() = _billUpdated

    private val _addDebtor = MutableLiveData<Event<Unit>>()
    val addDebtor: LiveData<Event<Unit>>
        get() = _addDebtor

    private val _changeCreditor = MutableLiveData<Event<Unit>>()
    val changeCreditor: LiveData<Event<Unit>>
        get() = _changeCreditor

    private val _availableMembers: LiveData<List<Member>> = Transformations.switchMap(group) { group ->
        groupsMembersRepository.getGroupMembersMembersByGroupName(group.name)
    }
    val availableMembers: LiveData<List<Member>>
        get() = _availableMembers

    private val debtorItemContainer = DebtorItemContainer(
            this,
            currency.value ?: Currency.getInstance("EUR"))

    val debtorItems = MediatorLiveData<List<DebtorItemViewModel>>().apply {
        addSource(group) {
            // Maybe unnecessary since it gets cleared on change of availableMembers
            debtorItemContainer.clear()
        }

        addSource(availableMembers) {
            val availableMembers = availableMembers.value
            val currency = currency.value

            if (availableMembers == null || currency == null) {
                return@addSource
            }

            debtorItemContainer.clear()
            debtorItemContainer.add(availableMembers)
        }

        addSource(amount) { amount ->
            if (!amount.isNullOrBlank()) {
                debtorItemContainer.isAmountValid = true
                debtorItemContainer.amount = BigDecimal(amount)
            } else {
                debtorItemContainer.amount = BigDecimal.ZERO
                debtorItemContainer.isAmountValid = false
            }
        }

        addSource(currency) { currency ->
            debtorItemContainer.currency = currency
        }
    }

    private val _availableDebtors = MediatorLiveData<List<Member>>().apply {
        addSource(availableMembers) { members ->
            debtorItems.value?.let {
                value = calcAvailableDebtors(members)
            }
        }

        addSource(debtorItems) {
            availableMembers.value?.let { members ->
                value = calcAvailableDebtors(members)
            }
        }
    }
    val availableDebtors: LiveData<List<Member>>
        get() = _availableDebtors


    val allGroupMembersAdded: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(availableMembers) { members ->
            debtorItems.value?.let { debtors ->
                value = calcAllGroupMembersAdded(members, debtors)
            }
        }

        addSource(debtorItems) { debtors ->
            availableMembers.value?.let { members ->
                value = calcAllGroupMembersAdded(members, debtors)
            }
        }
    }

    private fun calcAllGroupMembersAdded(members: List<Member>,
                                         debtors: List<DebtorItemViewModel>): Boolean {
        return members.size == debtors.size
    }

    val creditor = MediatorLiveData<Member>().apply {
        addSource(availableMembers) { members ->
            var userInGroup = false
            val user = getUser(appContext)
            members.forEach { member ->
                if (user.email == member.email) {
                    userInGroup = true
                }
            }
            value = if (userInGroup || members.isEmpty()) {
                user
            } else {
                members[0]
            }
        }
    }

    val groupItems = groupsRepository.getAll()

    private fun calcAvailableDebtors(availableMembers: List<Member>): List<Member> {
        val members: ArrayList<Member> = ArrayList()
        availableMembers.forEach { availableMember ->
            if (!debtorItemContainer.contains(availableMember.email)) {
                members.add(availableMember)
            }
        }
        return members
    }

    fun addDebtor() {
        _addDebtor.value = Event(Unit)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.frag_add_edit_bill_creditor_item -> changeCreditor()
        }
    }

    fun onContextItemSelected(item: MenuItem) = false

    fun removeDebtor(email: String) {
        debtorItemContainer.remove(email)
    }

    override fun onDebtorItemsChanged(debtorItems: List<DebtorItemViewModel>) {
        this.debtorItems.value = debtorItems
    }

    override fun onAmountToBalanceChanged(amountToBalance: BigDecimal) {
        this.amountToBalance.value = amountToBalance
    }

    override fun onPercentageToBalanceChanged(percentageToBalance: BigDecimal) {
        this.percentageToBalance.value = percentageToBalance
    }

    fun debtorAdded(member: Member) {
        debtorItemContainer.add(member)
    }

    private fun saveBill() {
        val currentIsAmountToBalance = isAmountToBalance.value ?: true
        val currentIsPercentageToBalance = isPercentageToBalance.value ?: true
        val currentCreditor = creditor.value
        val currentGroup = group.value
        val currentDescription = description.value
        val currentAmountString = amount.value
        val currentDate = Date()
        val currentDebtors = debtorItems.value
        val currentCurrency = currency.value ?: Currency.getInstance("EUR")

        if (currentCreditor == null || currentGroup == null) {
            // This case should never happen
            return
        }

        if (currentDescription.isNullOrBlank()) {
            _descriptionError.value = appContext.getString(R.string.enter_a_description)
            return
        }
        if (currentAmountString.isNullOrBlank()) {
            _amountError.value = appContext.getString(R.string.no_bill_amount_message)
            return
        }

        val currentAmount = converter.stringToBigDecimal(currentAmountString)
        if (currentAmount.compareTo(BigDecimal.ZERO) == 0) {
            _amountError.value = appContext.getString(R.string.no_bill_amount_message)
            return
        }

        if (currentDebtors == null || currentDebtors.isEmpty()) {
            _snackbarText.value = Event(R.string.one_debtor_should_be_added)
            return
        }

        if (currentIsAmountToBalance || currentIsPercentageToBalance) {
            // Should be always non null
            amountToBalancePretty.value?.let {
                _snackbarTextSpanned.value = Event(it)
            }
            return
        }

        val bill = Bill(currentDate,
                currentDescription,
                currentAmount,
                currentCurrency.currencyCode,
                currentCreditor.email,
                currentGroup.name,
                true)

        // FIXME: This is not multithread save
        val debtors = debtorItemContainer.debtorItemsToDebtors(bill.id)

        var debtorsAmount = BigDecimal.ZERO
        debtors.forEach { debtor ->
            debtorsAmount = debtorsAmount.add(debtor.amount)
        }

        val billDebtors = BillDebtors()
        billDebtors.bill = bill
        billDebtors.debtors = debtors

        debtorsRepository.createNewBill(billDebtors)
        _billUpdated.value = Event(bill)
    }

    fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save_bill -> {
                saveBill()
                false
            }
            else -> false
        }
    }

    private fun changeCreditor() {
        _changeCreditor.value = Event(Unit)
    }


    enum class SplitMode {
        ABSOLUTE,
        PERCENTAGE
    }

    companion object {
        private val TAG = "AddEditBillViewModel"
    }
}