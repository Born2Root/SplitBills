package org.weilbach.splitbills.addeditbill

import android.content.Context
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.*
import org.weilbach.splitbills.Event
import org.weilbach.splitbills.MATH_CONTEXT
import org.weilbach.splitbills.MemberItemNavigator
import org.weilbach.splitbills.R
import org.weilbach.splitbills.data.*
import org.weilbach.splitbills.data.local.Converter
import org.weilbach.splitbills.data.source.*
import org.weilbach.splitbills.util.AppExecutors
import org.weilbach.splitbills.util.getCurrency
import org.weilbach.splitbills.util.getUser
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
        private val appContext: Context
) : ViewModel(), MemberItemNavigator {

    private val converter = Converter()

    private val _currencyItems = MutableLiveData<List<Currency>>().apply {
        value = Currency.getAvailableCurrencies().toList()
    }
    val currencyItems: LiveData<List<Currency>>
        get() = _currencyItems

    val debtorContainer = HashMap<String, Member>()

    val description = MutableLiveData<String>()

    val amount = MutableLiveData<String>()

    val posCurrencySpinner = MutableLiveData<Int>().apply {
        val currency = getCurrency(appContext)
        Currency.getAvailableCurrencies().indexOf(currency).let {
            value = it
        }
    }

    val currency: LiveData<Currency> = Transformations.switchMap(posCurrencySpinner) { pos ->
        var currency = Currency.getInstance("EUR")

        currencyItems.value?.let { currencies ->
            if (pos < currencies.size) {
                currency = currencies[pos]
            }
        }

        MutableLiveData<Currency>().apply {
            value = currency
        }
    }

    val posGroupSpinner = MutableLiveData<Int>()

    val group: LiveData<Group> = Transformations.switchMap(posGroupSpinner) { pos ->
        var group = Group("")
        groupItems.value?.let { groups ->
            group = groups[pos]
        }
        MutableLiveData<Group>().apply {
            value = group
        }
    }

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarMessage: LiveData<Event<Int>>
        get() = _snackbarText

    private val _billUpdated = MutableLiveData<Event<Unit>>()
    val billUpdatedEvent: LiveData<Event<Unit>>
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

    val debtorItems = MediatorLiveData<ArrayList<Member>>().apply {
        addSource(availableMembers) {
            availableMembers.value?.let { members ->
                members.forEach { member ->
                    debtorContainer[member.email] = member
                }
                value = ArrayList(members)
            }
        }
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

    fun addDebtor() {
        _addDebtor.value = Event(Unit)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.frag_add_edit_bill_creditor_item -> changeCreditor()
        }
    }

    fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            0 -> {
                val pos = item.order
                debtorItems.value?.get(pos)?.let {
                    debtorContainer.remove(it.email)
                }
                debtorItems.value?.removeAt(pos)
                debtorItems.value = debtorItems.value
            }
        }
        return false
    }

    fun debtorAdded(member: Member) {
        if (debtorContainer.contains(member.email)) {
            _snackbarText.value = Event(R.string.debtor_already_added)
            return
        }
        debtorContainer[member.email] = member
        updateDebtorItems()
        _snackbarText.value = Event(R.string.member_successful_added)
    }

    private fun updateDebtorItems() {
        debtorItems.value = ArrayList(debtorContainer.values)
    }

    fun saveBill() {
        val currentCreditor = creditor.value
        val currentGroup = group.value
        val currentDescription = description.value
        val currentAmountString = amount.value
        val currentDate = Date()
        val currentCurrency = currency.value ?: Currency.getInstance("EUR")

        if (currentDescription == null) {
            _snackbarText.value = Event(R.string.no_bill_description_message)
            return
        }
        if (currentAmountString == null) {
            _snackbarText.value = Event(R.string.no_bill_amount_message)
            return
        }
        val currentAmount = converter.stringToBigDecimal(currentAmountString)

        if (debtorContainer.isEmpty()) {
            _snackbarText.value = Event(R.string.one_debtor_should_be_added)
            return
        }

        // TODO: Fix !!
        val bill = Bill(currentDate,
                currentDescription,
                currentAmount,
                currentCurrency.currencyCode,
                currentCreditor!!.email,
                currentGroup!!.name,
                true)

        val debtorAmount = currentAmount.divide(BigDecimal(debtorContainer.size), MATH_CONTEXT)

        val debtors = debtorContainer.map { entry ->
            Debtor(bill.id, entry.value.email, debtorAmount)
        }

        val billDebtors = BillDebtors()
        billDebtors.bill = bill
        billDebtors.debtors = debtors

        debtorsRepository.createNewBill(billDebtors)
        _billUpdated.value = Event(Unit)
    }

    private fun changeCreditor() {
        _changeCreditor.value = Event(Unit)
    }

    companion object {
        private val TAG = "AddEditBillViewModel"
    }
}