package org.weilbach.splitbills.addeditbill

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.*
import org.weilbach.splitbills.Event
import org.weilbach.splitbills.MATH_CONTEXT
import org.weilbach.splitbills.MemberItemNavigator
import org.weilbach.splitbills.R
import org.weilbach.splitbills.data2.*
import org.weilbach.splitbills.data2.local.Converter
import org.weilbach.splitbills.data2.source.*
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

    //val debtorContainer = MemberContainer()
    val debtorContainer = HashMap<String, Member>()

    val description = MutableLiveData<String>()

    val amount = MutableLiveData<String>()

    /*val creditor = MutableLiveData<Member>().apply {
        value = Member("Default MemberData", "default@member.org")
    }*/

    /*private val _currency = MutableLiveData<Currency>().apply {
        value = Currency.getInstance("EUR")
    }
    val currency: LiveData<Currency>
        get() = _currency*/


    /*val posCurrencySpinner = MutableLiveData<Int>().apply {
        value = 0
    }*/

    /*val posCurrencySpinner: LiveData<Int> = Transformations.switchMap(currencyItems) { currencies ->
        var index = 0
        val currency = getCurrency(appContext)
        currencies.indexOf(currency).let {
            index = it
        }
        MutableLiveData<Int>().apply {
            value = index
        }
    }*/

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

    val posGroupSpinner = MutableLiveData<Int>()/*.apply {
        observeForever { pos ->
            if (_groupItems.value != null) {
                if (_groupItems.value!!.size > pos) {
                    _group.value = _groupItems.value!![pos]
                }
            }
        }
    }*/

    /*private val _group = MutableLiveData<Group>().apply {
        value = Group("No group")
    }
    val group: LiveData<Group>
        get() = _group*/

    val group: LiveData<Group> = Transformations.switchMap(posGroupSpinner) { pos ->
        var group = Group("")
        groupItems.value?.let { groups ->
            group = groups[pos]
        }
        MutableLiveData<Group>().apply {
            value = group
        }
    }

    // val group = MutableLiveData<Group>()

    /*private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean>
        get() = _dataLoading*/

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

    /*val debtorItems = MutableLiveData<ArrayList<Member>>().apply {
        value = ArrayList()
    }*/

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

    /*val creditor: LiveData<Member> = Transformations.switchMap(availableMembers) { members ->
        var userInGroup = false
        val user = getUser(appContext)
        members.forEach { member ->
            if (user.email == member.email) {
                userInGroup = true
            }
        }
        if (userInGroup || members.isEmpty()) {
            membersRepository.getMemberByEmail(user.email)
        } else {
            membersRepository.getMemberByEmail(members[0].email)
        }
    }*/
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

    /*private val _groupItems = MutableLiveData<List<Group>>().apply {
        value = ArrayList()
    }*/
    /*private val _groupItems = groupsRepository.getAll()
    val groupItems: LiveData<List<Group>>
        get() = _groupItems*/

    val groupItems = groupsRepository.getAll()

    fun addDebtor() {
        _addDebtor.value = Event(Unit)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.frag_add_edit_bill_creditor_item -> changeCreditor()
        }
    }

    fun start() {
        //setupGroupItems()
        // setCreditor()
        // setUpCurrencySpinner()

        /*if (groupItems.value!!.isNotEmpty()) {
            fillAvailableMembers(groupItems.value!![0].name)
            setUpDebtors(groupItems.value!![0].name)
        }*/

        // FIXME: Lifecycle?
        /*posGroupSpinner.observeForever { pos ->
            _groupItems.value?.let { groups ->
                if (groups.size <= pos) return@observeForever

                _group.value = groups[pos]

                _group.value?.let {
                    fillAvailableMembers(it.name)
                }

                setCreditor()
            }
        }*/
       /* posCurrencySpinner.observeForever { pos ->
            _currencyItems.value?.let { currencies ->
                if (currencies.size <= pos) return@observeForever
                _currency.value = currencies[pos]
            }
        }*/

        /*_dataLoading.value?.let { isLoading ->
            if (isLoading) return
        }*/
    }

    private fun setUpDebtors(groupName: String) {
        /*   groupsMembersRepository.getGroupMembersByGroupName(groupName, object : GroupsMembersDataSource.GetGroupsMembersCallback {
               override fun onGroupsMembersLoaded(groupsMemberData: List<GroupMemberData>) {
                   groupsMemberData.forEach { groupMember ->
                       membersRepository.getMember(groupMember.memberEmail, object : MembersDataSource.GetMemberCallback {
                           override fun onMemberLoaded(memberData: MemberData) {
                               debtorContainer.add(Member(memberData.name, memberData.email))
                               updateDebtorItems()
                           }

                           override fun onDataNotAvailable() {
                               Log.w(TAG, "Members not available")
                           }
                       })
                   }
               }

               override fun onDataNotAvailable() {
                   Log.w(TAG, "No group bills available")
               }
           })*/
    }

    /*private fun setUpCurrencySpinner() {
        val currency = getCurrency(appContext)
        currencyItems.value?.indexOf(currency)?.let {
            posCurrencySpinner.value = it
        }
    }*/

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

    /*private fun setCreditor() {
        _group.value?.let { group ->

        }*/
/*        _group.value?.let { group ->
            groupsMembersRepository.getGroupMembersByGroupName(group.name, object : GroupsMembersDataSource.GetGroupsMembersCallback {
                override fun onGroupsMembersLoaded(groupsMemberData: List<GroupMemberData>) {
                    val user = getUser(appContext)
                    Log.d(TAG, "user: $user")
                    Log.d(TAG, "groupsMembers: $groupsMemberData")

                    val res = groupsMemberData.contains(GroupMemberData(group.name, user.email))

                    if (res) {
                        Log.d(TAG, "user in group, set as creditor")
                        creditor.value = user
                        return
                    }

                    groupsMembersRepository.getGroupMembersByGroupName(group.name, object : GroupsMembersDataSource.GetGroupsMembersCallback {
                        override fun onGroupsMembersLoaded(groupsMemberData: List<GroupMemberData>) {
                            membersRepository.getMember(groupsMemberData[0].memberEmail, object : MembersDataSource.GetMemberCallback {
                                override fun onMemberLoaded(memberData: MemberData) {
                                    Log.d(TAG, "user not in group, set default member as creditor")
                                    creditor.value = Member(memberData.name, memberData.email)
                                }

                                override fun onDataNotAvailable() {
                                    creditor.value = Member("No bills in group", "no.bills@group.org")
                                }
                            })
                        }

                        override fun onDataNotAvailable() {
                            Log.d(TAG, "No group bills found")
                            creditor.value = Member("No bills in group", "no.bills@group.org")
                        }
                    })
                }

                override fun onDataNotAvailable() {
                    creditor.value = Member("No bills in group", "no.bills@group.org")
                }
            })
        }*/
    //}

    private fun setupGroupItems() {
/*        groupRepository.getGroups(object : GroupsDataSource.GetGroupsCallback {
            override fun onGroupsLoaded(group: List<GroupData>) {
                _groupItems.value = group
            }

            override fun onDataNotAvailable() {
                Log.w(TAG, "GroupData items could not be loaded")
            }
        })*/
    }

    private fun fillAvailableMembers(groupName: String) {
/*        val members = ArrayList<Member>()

        groupsMembersRepository.getGroupMembersByGroupName(groupName, object : GroupsMembersDataSource.GetGroupsMembersCallback {
            override fun onGroupsMembersLoaded(groupsMemberData: List<GroupMemberData>) {
                groupsMemberData.forEach { groupMember ->
                    membersRepository.getMember(groupMember.memberEmail, object : MembersDataSource.GetMemberCallback {
                        override fun onMemberLoaded(memberData: MemberData) {
                             members.add(Member(memberData.name, memberData.email))
                        }

                        override fun onDataNotAvailable() {
                            Log.w(TAG, "Members not available")
                        }
                    })
                }
            }

            override fun onDataNotAvailable() {
                Log.w(TAG, "No group bills available")
            }
        })
        _availableMembers.value = members*/
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
        val currentDate = Date()// BillData.formatter.format(Date())
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

    private fun saveAmount(billId: String, amount: String, currency: String) {
/*        amountsRepository.getAmountsByBillId(billId, object : AmountsDataSource.GetAmountsCallback {
            override fun onAmountsLoaded(amountData: List<AmountData>) {
                amountData.forEach { amount ->
                    amountsRepository.saveAmount(AmountData(
                            amount.billId,
                            amount.amount,
                            amount.currency,
                            false))
                }
            }

            override fun onDataNotAvailable() {
                Log.w(TAG, "No amounts available")
            }
        })
        amountsRepository.saveAmount(AmountData(billId, amount, currency,true))*/
    }

    private fun saveDebtors(billId: String, debtorContainer: HashMap<String, Member>) {
/*        debtorContainer.forEach { debtor ->
            debtorsRepository.saveDebtor(DebtorData(billId, debtor.id))
            //_debtorUpdated.value = Event(Unit)
        }*/
    }

    fun handleActivityOnResult(requestCode: Int, resultCode: Int, data: Intent?) {
        /*if (requestCode == AddEditBillActivity.REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val name = data?.getStringExtra(AddMemberActivity.RESULT_MEMBER_NAME)
                    val email = data?.getStringExtra(AddMemberActivity.RESULT_MEMBER_EMAIL)
                    memberAdded(name!!, email!!)
                }
            }
        }*/
    }

    fun changeCreditor() {
        _changeCreditor.value = Event(Unit)
    }

    companion object {
        private val TAG = "AddEditBillViewModel"
    }
}