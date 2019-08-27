package org.weilbach.splitbills.addeditbill

import android.content.Context
import android.text.Spanned
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.*
import com.google.common.collect.Range.range
import org.weilbach.splitbills.*
import org.weilbach.splitbills.R
import org.weilbach.splitbills.data.*
import org.weilbach.splitbills.data.local.Converter
import org.weilbach.splitbills.data.source.*
import org.weilbach.splitbills.util.*
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap



class AddEditBillViewModel(
        private val groupsRepository: GroupRepository,
        private val billsRepository: BillRepository,
        private val debtorsRepository: DebtorRepository,
        private val membersRepository: MemberRepository,
        private val groupsMembersRepository: GroupMemberRepository,
        private val appExecutors: AppExecutors,
        val appContext: Context
) : ViewModel(), MemberItemNavigator {

    private val converter = Converter()

    private val _changeSplitModeEvent = MutableLiveData<Event<SplitMode>>()
    val changeSplitModeEvent: LiveData<Event<SplitMode>>
        get() = _changeSplitModeEvent

    private val _currencyItems = MutableLiveData<List<Currency>>().apply {
        value = Currency.getAvailableCurrencies().toList()
    }
    val currencyItems: LiveData<List<Currency>>
        get() = _currencyItems

    val description = MutableLiveData<String>()

    val amountToBalance = MutableLiveData<Spanned>()

    val isAmountToBalance = MutableLiveData<Boolean>().apply {
        value = false
    }

    private val _splitMode = MutableLiveData<SplitMode>().apply {
        value = SplitMode.ABSOLUTE
    }
    val splitMode: LiveData<SplitMode>
        get() = _splitMode

    private val _splitModeString = Transformations.map(splitMode) { splitMode ->
        when (splitMode) {
            SplitMode.ABSOLUTE -> {
                R.string.absolute
            }
            SplitMode.PERCENTAGE -> {
                R.string.percentage
            }
            else -> {
                R.string.percentage
            }
        }
    }
    val splitModeString: LiveData<Int>
        get() = _splitModeString


    private val _descriptionError = MutableLiveData<String>()
    val descriptionError: LiveData<String>
        get() = _descriptionError

    val amount = MutableLiveData<String>()

    private val _amountError = MutableLiveData<String>()
    val amountError: LiveData<String>
        get() = _amountError

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
        currency
    }

    val currencySymbol: LiveData<String> = Transformations.map(currency) { currency ->
        currency.symbol
    }

    val posGroupSpinner = MutableLiveData<Int>()

    val group: LiveData<Group> = Transformations.map(posGroupSpinner) { pos ->
        var group = Group("")
        groupItems.value?.let { groups ->
            debtorItems.value?.clear()
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

    val debtorItems = MediatorLiveData<LinkedHashMap<String, MemberWithAmount>>().apply {
        addSource(availableMembers) {
            availableMembers.value?.let { members ->
                val debtors = LinkedHashMap<String, MemberWithAmount>()
                members.forEach { member ->
                    debtors[member.email] = MemberWithAmount(
                            member.name,
                            member.email,
                            this@AddEditBillViewModel)
                }
                value = debtors
            }
        }

        addSource(amount) { amount ->
            value?.let { debtors ->
                var amountString = amount
                if (amountString.isBlank()) {
                    amountString = "0"
                    // FIXME: Workaround since live data not working as expected
                    debtors.forEach {
                        it.value.amountButtonValid.value = false
                    }
                } else {
                    debtors.forEach {
                        it.value.amountButtonValid.value = true
                    }
                }
                val amount = converter.stringToBigDecimal(amountString)
                if (debtors.size == 0) {
                    return@let
                }
                val debtorsSize = BigDecimal(debtors.size)
                val debtorAmount = amount.divide(debtorsSize, MATH_CONTEXT)
                        .setScale(SCALE, ROUNDING_MODE)
                val step = BigDecimal("0.01").setScale(SCALE)

                debtors.forEach { debtor ->
                    debtor.value.amount = debtorAmount
                }

                val random = Random()
                while (true) {
                    var debtorAmountSum = BigDecimal.ZERO
                    debtors.forEach { debtor ->
                        debtorAmountSum = debtorAmountSum.add(debtor.value.amount)
                    }
                    debtorAmountSum.setScale(SCALE, ROUNDING_MODE)

                    val debtorsList = debtors.values.toList()
                    val index = random.nextInt(debtorsList.size)
                    if (debtorAmountSum.compareTo(amount) == 1) {
                        val debtor = debtors[debtorsList[index].email]
                        // Should always be non null
                        debtor!!.amount = debtor.amount.subtract(step)
                    } else if (debtorAmountSum.compareTo(amount) == -1) {
                        val debtor = debtors[debtorsList[index].email]
                        // Should always be non null
                        debtor!!.amount = debtor.amount.add(step)
                    } else {
                        break
                    }
                }
                value = value
                setBalanceAmountDebtors()
            }
        }
    }

    private val _availableDebtors = MediatorLiveData<List<Member>>().apply {
        addSource(availableMembers) { members ->
            debtorItems.value?.let { debtors ->
                value = calcAvailableDebtors(members, debtors)
            }
        }
        addSource(debtorItems) { debtors ->
            availableMembers.value?.let { members ->
                value = calcAvailableDebtors(members, debtors)
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
                                         debtors: HashMap<String, MemberWithAmount>): Boolean {
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

    private fun calcAvailableDebtors(availableMembers: List<Member>,
                                     debtors: HashMap<String, MemberWithAmount>): List<Member> {
        val members: ArrayList<Member> = ArrayList()
        availableMembers.forEach { availableMember ->
            if (!debtors.contains(availableMember.email)) {
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
        debtorItems.value?.remove(email)
        debtorItems.value = debtorItems.value
        val amount = amount.value
        if (amount.isNullOrBlank()) {
            return
        }

        setBalanceAmountDebtors()
    }

    private fun setBalanceAmountDebtors() {
        setBalanceAmountDebtors { _, _ -> }
    }

    fun setBalanceAmountDebtors(after: (BigDecimal, String) -> Unit) {
        val amount = amount.value
        if (amount.isNullOrBlank()) {
            return
        }
        var amountSum = BigDecimal.ZERO
        debtorItems.value?.forEach { entry ->
            amountSum = amountSum.add(entry.value.amount)
        }

        val currencySymbol = currencySymbol.value ?: ""
        val balance = BigDecimal(amount)
                .subtract(amountSum)
                .setScale(SCALE, ROUNDING_MODE)
        isAmountToBalance.value = balance.compareTo(BigDecimal.ZERO) != 0
        amountToBalance.value =
                fromHtml(appContext.getString(R.string.balance_debtors, balance, currencySymbol))

        after(balance, currencySymbol)
    }

    fun debtorAdded(member: Member) {
        debtorItems.value?.let { debtors ->
            if (debtors.contains(member.email)) {
                return
            }
            val newDebtor = MemberWithAmount(member.name, member.email, this)
            debtors[member.email] = newDebtor
            val amount = amount.value
            if (!amount.isNullOrBlank()) {
                newDebtor.amount = BigDecimal.ZERO
                // FIXME: Workaround since live data is not working as expected
                newDebtor.amountButtonValid.value = true
            }
            updateDebtorItems()
        }
    }

    private fun updateDebtorItems() {
        debtorItems.value = debtorItems.value // ArrayList(debtorContainer.values)
    }

    private fun saveBill() {
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

        if (currentDebtors.isNullOrEmpty()) {
            _snackbarText.value = Event(R.string.one_debtor_should_be_added)
            return
        }

        val bill = Bill(currentDate,
                currentDescription,
                currentAmount,
                currentCurrency.currencyCode,
                currentCreditor.email,
                currentGroup.name,
                true)

        var debtorsAmount = BigDecimal.ZERO
        val debtors = currentDebtors.filter { entry ->
            entry.value.amount.compareTo(BigDecimal.ZERO) != 0 }.map { entry ->
            val debtorAmount = entry.value.amount
            debtorsAmount = debtorsAmount.add(debtorAmount)
            Debtor(bill.id, entry.value.email, debtorAmount)
        }

        if (debtorsAmount.compareTo(currentAmount) != 0) {
            setBalanceAmountDebtors { balance, currencySymbol ->
                _snackbarTextSpanned.value = Event(fromHtml(appContext.getString(R.string.balance_debtors, balance, currencySymbol)))
            }
            return
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

    fun onChangeSplitModeButtonClicked() {
        splitMode.value?.let {
            _changeSplitModeEvent.value = Event(it)
        }
    }

    enum class SplitMode {
        ABSOLUTE,
        PERCENTAGE
    }

    companion object {
        private val TAG = "AddEditBillViewModel"
    }
}