package org.weilbach.splitbills.group

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.view.MenuItem
import androidx.lifecycle.*
import org.weilbach.splitbills.*
import org.weilbach.splitbills.R
import org.weilbach.splitbills.addeditbill.AddEditBillActivity
import org.weilbach.splitbills.data2.*
import org.weilbach.splitbills.data2.source.*
import org.weilbach.splitbills.util.ADD_EDIT_RESULT_OK
import org.weilbach.splitbills.util.AppExecutors
import org.weilbach.splitbills.util.getCurrency
import org.weilbach.splitbills.util.getUser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.math.BigDecimal

class GroupViewModel(val groupRepository: GroupRepository,
                     val memberRepository: MemberRepository,
                     val groupMemberRepository: GroupMemberRepository,
                     val billRepository: BillRepository,
                     val debtorRepository: DebtorRepository,
                     val appExecutors: AppExecutors,
                     val appContext: Context) : ViewModel() {

    /*private val _items = MutableLiveData<List<Group>>().apply {
        value = emptyList()
    }
    val items: LiveData<List<Group>>
        get() = _items*/

    private val _groupMerging = MutableLiveData<Boolean>().apply {
        value = false
    }
    val groupMerging: LiveData<Boolean>
    get() = _groupMerging

    private val user = getUser(appContext)
    private val currency = getCurrency(appContext)

    val items = groupRepository.getAll()

    private val _openGroupEvent = MutableLiveData<Event<String>>()
    val openGroupEvent: LiveData<Event<String>>
        get() = _openGroupEvent

    private val _groupMergeStartedEvent = MutableLiveData<Event<Unit>>()
    val groupMergeStartedEvent: LiveData<Event<Unit>>
        get() = _groupMergeStartedEvent

    private val _groupMergeFailed = MutableLiveData<Event<Unit>>()
    val groupMergeFailed: LiveData<Event<Unit>>
        get() = _groupMergeFailed

    private val _groupAddedEvent = MutableLiveData<Event<Unit>>()
    val groupAddedEvent: LiveData<Event<Unit>>
        get() = _groupAddedEvent

    private val _groupMergedEvent = MutableLiveData<Event<Unit>>()
    val groupMergedEvent: LiveData<Event<Unit>>
        get() = _groupMergedEvent

    private val _newGroupEvent = MutableLiveData<Event<Unit>>()
    val newGroupEvent: LiveData<Event<Unit>>
        get() = _newGroupEvent

    private val _billUpdatedEvent = MutableLiveData<Event<Unit>>()
    val billUpdatedEvent: LiveData<Event<Unit>>
        get() = _billUpdatedEvent

    private val _newBillEvent = MutableLiveData<Event<Unit>>()
    val newBillEvent: LiveData<Event<Unit>>
        get() = _newBillEvent

    private val _dataLoading = MutableLiveData<Boolean>().apply { value = false }
    val dataLoading: LiveData<Boolean>
        get() = _dataLoading

    /*private val totalOwe = MutableLiveData<BigDecimal>()*/

    private val groupsWithMembersAndBillsDebtors = groupRepository.getGroupsWithMembersAndBillsWithDebtors()

    private val totalOwe: LiveData<BigDecimal> = Transformations.map(groupsWithMembersAndBillsDebtors) { groupsWithMembersAndBillsDebtors ->
        var res = BigDecimal.ZERO
        groupsWithMembersAndBillsDebtors.forEach { groupWithMembersAndBillsDebtors ->
            res = res.add(memberOwesGroupTotal(user, groupWithMembersAndBillsDebtors))
        }
        res
    }

    // private val totalGet = MutableLiveData<BigDecimal>()

    private val totalGet: LiveData<BigDecimal> = Transformations.map(groupsWithMembersAndBillsDebtors) { groupsWithMembersAndBillsDebtors ->
        var res = BigDecimal.ZERO
        groupsWithMembersAndBillsDebtors.forEach { groupWithMembersAndBillsDebtors ->
            res = res.add(memberGetsFromGroupTotal(user, groupWithMembersAndBillsDebtors))
        }
        res
    }

    private val _oweGetTotal = MediatorLiveData<String>().apply {
        addSource(totalOwe) { owe ->
            totalGet.value?.let { get ->
                val res = get.subtract(owe)
                when (res.compareTo(BigDecimal.ZERO)) {
                    0 -> {
                        _totalBalanceColor.value = R.color.colorGet
                        value = appContext.getString(R.string.you_are_settled_up)
                    }
                    1 -> {
                        _totalBalanceColor.value = R.color.colorGet
                        value = appContext.getString(R.string.you_get, prettyPrintNum(res), currency.symbol)
                    }
                    -1 -> {
                        _totalBalanceColor.value = R.color.colorOwe
                        value = appContext.getString(R.string.you_owe, prettyPrintNum(res.abs()), currency.symbol)
                    }
                }
            }
        }
        addSource(totalGet) { get ->
            totalOwe.value?.let { owe ->
                val res = get.subtract(owe)
                when (res.compareTo(BigDecimal.ZERO)) {
                    0 -> {
                        _totalBalanceColor.value = R.color.colorGet
                        value = appContext.getString(R.string.you_are_settled_up)
                    }
                    1 -> {
                        _totalBalanceColor.value = R.color.colorGet
                        value = appContext.getString(R.string.you_get, prettyPrintNum(res), currency.symbol)
                    }
                    -1 -> {
                        _totalBalanceColor.value = R.color.colorOwe
                        value = appContext.getString(R.string.you_owe, prettyPrintNum(res.abs()), currency.symbol)
                    }
                }
            }
        }
    }
    val oweGetTotal: LiveData<String>
        get() = _oweGetTotal

    /*private val _groupsAddViewVisible = MutableLiveData<Boolean>().apply { value = true }
    val groupsAddViewVisible: LiveData<Boolean>
        get() = _groupsAddViewVisible*/

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarMessage: LiveData<Event<Int>>
        get() = _snackbarText

    private val _totalBalanceColor = MutableLiveData<Int>().apply {
        value = R.color.colorGet
    }
    val totalBalanceColor: LiveData<Int>
        get() = _totalBalanceColor

    val itemsEmpty: LiveData<Boolean> = Transformations.map(items) {
        it.isEmpty()
    }

    /*val empty: LiveData<Boolean> = Transformations.map(items) { it.isEmpty() }*/

    /**
     * Start the view model.
     * Initialise the view model.
     */
    fun start() {
        loadGroups(true)

        /*groupRepository.getGroupsWithMembersAndBillsWithDebtors().observeForever {
            it.forEach { groupMembersBillsDebtors ->
                totalOwe.value = totalOwe.value?.add(memberOwesGroupTotal(user, groupMembersBillsDebtors))
                        ?: memberOwesGroupTotal(user, groupMembersBillsDebtors)

                totalGet.value = totalGet.value?.add(memberGetsFromGroupTotal(user, groupMembersBillsDebtors))
                        ?: memberGetsFromGroupTotal(user, groupMembersBillsDebtors)
            }
        }*/

/*        items.observeForever { groups ->
            groups.forEach { group ->

                groupRepository.getGroupWithMembersAndBillsWithDebtorsByName(group.name).observeForever { groupMembersBillsDebtors ->
                    totalOwe.value = totalOwe.value?.add(memberOwesGroupTotal(user, groupMembersBillsDebtors))
                            ?: memberOwesGroupTotal(user, groupMembersBillsDebtors)
                    totalGet.value = totalGet.value?.add(memberGetsFromGroupTotal(user, groupMembersBillsDebtors))
                            ?: memberGetsFromGroupTotal(user, groupMembersBillsDebtors)
                }
            }
        }*/
    }

    fun loadGroups(forceUpdate: Boolean) {
        loadGroups(forceUpdate, true)
    }

    fun addNewGroup() {
        _newGroupEvent.value = Event(Unit)
    }

    fun addNewBill() {
        _newBillEvent.value = Event(Unit)
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        when (requestCode) {
            AddEditBillActivity.REQUEST_CODE -> {
                if (resultCode == ADD_EDIT_RESULT_OK) {
                    _snackbarText.value = Event(R.string.bill_saved)
                }
                // Should not be needed anymore because of live data
                // loadGroups(true)
                // _billUpdatedEvent.value = Event(Unit)
                // updateOweGetTotal()
            }
            GroupActivity.IMPORT_GROUP_REQUEST -> {
                if (resultCode == RESULT_OK) {
                    intent?.let {
                        importGroup(it.data)
                    }
                }
            }
        }
    }

    fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            0 -> removeGroup(item)
        }
        return false
    }

    fun removeGroup(item: MenuItem) {
        val pos = item.order

        // TODO: Present a warning dialog

        items.value?.let {
            if (pos >= it.size) {
                return@let
            }
            val group = it[pos]

            groupRepository.deleteGroup(group.name)

            // TODO: Refresh view
        }

        /*_items.value?.let {
            if (pos >= it.size) return@let
            val newItems = LinkedList(it)
            newItems.removeAt(pos)
            _items.value = newItems
        }*/
    }

    /*private fun removeBillsSync(group: GroupData) {
        billRepository.getBillsByGroupNameSync(group.name).forEach { bill ->
            debtorRepository.deleteDebtorsByBillIdSync(bill.id)
*//*            amountsRepository.deleteAmountsByBillIdSync(bill.id)*//*
            billRepository.deleteBillSync(bill.id)
        }
    }*/

    /*private fun updateOweGetTotal() {
        *//*billRepository.refresh()
        groupRepository.refresh()
        debtorRepository.refresh()*//*
        // groupMemberRepository.refresh()

        groupRepository.getAll().observeForever { groups ->
            groups.forEach { group ->

                groupRepository.getGroupWithMembersAndBillsWithDebtorsByName(group.name).observeForever { groupMembersBillsDebtors ->
                    totalOwe.value = totalOwe.value?.add(memberOwesGroupTotal(user, groupMembersBillsDebtors))
                            ?: memberOwesGroupTotal(user, groupMembersBillsDebtors)
                    totalGet.value = totalGet.value?.add(memberGetsFromGroupTotal(user, groupMembersBillsDebtors))
                            ?: memberGetsFromGroupTotal(user, groupMembersBillsDebtors)
                }
            }
        }

        *//*billRepository.refresh()
        groupRepository.refresh()
        debtorRepository.refresh()
        groupMemberRepository.refresh()

        val u = getUser(appContext)
        val user = Member(u.name, u.email)
        Log.d(TAG, "user: $user")

        var totalOwe = BigDecimal.ZERO
        var totalGet = BigDecimal.ZERO

        appExecutors.diskIO.execute {
            val groups = groupRepository.getGroupsSync()
            groups.forEach { group ->

                val debtorsByBillId = HashMap<String, List<DebtorData>>()
                *//**//*val amountByBillId = HashMap<String, AmountData>()*//**//*

                val bills = validBills(billRepository.getBillsByGroupNameSync(group.name))

                bills.forEach { bill ->
                    debtorsByBillId[bill.id] = debtorRepository.getDebtorsByBillIdSync(bill.id)
*//**//*                    val amount = amountsRepository.getValidAmountByBillIdSync(bill.id)
                    if (amount != null) {
                        amountByBillId[bill.id] = amount
                    }*//**//*
                }

                totalOwe += memberOwesTotal(user, bills, debtorsByBillId)
                totalGet += memberGetsTotal(user, bills, debtorsByBillId)
            }

            appExecutors.mainThread.execute {
                val currency = getCurrency(appContext)
                val res = totalGet.subtract(totalOwe)
                when (res.compareTo(BigDecimal.ZERO)) {
                    0 -> {
                        _totalBalanceColor.value = R.color.colorGet
                        _oweGetTotal.value = appContext.getString(R.string.you_are_settled_up)
                    }
                    1 -> {
                        _totalBalanceColor.value = R.color.colorGet
                        _oweGetTotal.value = appContext.getString(R.string.you_get, prettyPrintNum(res), currency.symbol)
                    }
                    -1 -> {
                        _totalBalanceColor.value = R.color.colorOwe
                        _oweGetTotal.value = appContext.getString(R.string.you_owe, prettyPrintNum(res.abs()), currency.symbol)
                    }
                }
            }
        }*//*
    }*/

    fun handleIntent(intent: Intent) {
        intent.action?.let {
            when (it) {
                Intent.ACTION_VIEW -> {
                    intent.data?.let { data ->
                        importGroup(data)
                    }
                }
                else -> {

                }
            }
        }
    }

    private fun importGroup(uri: Uri) {
        _groupMerging.value = true
        _groupMergeStartedEvent.value = Event(Unit)
        val inputStream = appContext.contentResolver.openInputStream(uri)
        inputStream?.let {
            _snackbarText.value = Event(R.string.trying_to_merge)
            var res: Triple<GroupMembersBillsDebtors, List<Member>, String>? = null

            try {
                res = importGroupFromXml(it)
            } catch (e: IOException) {
                _groupMerging.value = false
                appExecutors.mainThread.execute {
                    _snackbarText.value = Event(R.string.could_not_merge_group_io_error)
                }
                _groupMergeFailed.value = Event(Unit)
                return
            } catch (e: XmlPullParserException) {
                _groupMerging.value = false
                appExecutors.mainThread.execute {
                    _snackbarText.value = Event(R.string.could_not_merge_no_group)
                }
                return
            }

            MergeGroupTask(
                    res.first,
                    res.second,
                    groupRepository,
                    billRepository,
                    debtorRepository,
                    memberRepository,
                    groupMemberRepository,
                    _snackbarText,
                    _groupMergedEvent,
                    _groupAddedEvent,
                    _groupMerging).execute()

            /*try {
                val group = importGroupFromXml(it)
                appExecutors.diskIO.execute {
                    Log.d(TAG, "merge group")
                    mergeGroup(group,
                            groupRepository,
                            memberRepository,
                            groupMemberRepository,
                            billRepository,
                            debtorRepository, {
                        appExecutors.mainThread.execute {
                            loadGroups(forceUpdate = true, showLoadingUI = true)
                            updateOweGetTotal()
                            _snackbarText.value = Event(R.string.merged_group)
                        }
                    }, {
                        Log.d(TAG, "add group")
                        addGroup(group,
                                groupRepository,
                                memberRepository,
                                groupMemberRepository,
                                billRepository,
                                debtorRepository) {
                            appExecutors.mainThread.execute {
                                loadGroups(forceUpdate = true, showLoadingUI = true)
                                updateOweGetTotal()
                                _snackbarText.value = Event(R.string.added_group)
                            }
                        }
                    })
                }
            } catch (e: IOException) {
                appExecutors.mainThread.execute {
                    _snackbarText.value = Event(R.string.could_not_merge_group_io_error)
                }
            } catch (e: XmlPullParserException) {
                appExecutors.mainThread.execute {
                    _snackbarText.value = Event(R.string.could_not_merge_no_group)
                }
            }*/
        }
    }

    private fun loadGroups(forceUpdate: Boolean, showLoadingUI: Boolean) {
        if (showLoadingUI) {
            _dataLoading.value = true
        }
        if (forceUpdate) {
            groupRepository.refresh()
        }

        _dataLoading.value = false

        /*
        groupRepository.getGroups(object : GroupsDataSource.GetGroupsCallback {
            override fun onGroupsLoaded(group: List<GroupData>) {
                val groupsToShow = ArrayList<GroupData>()

                for (group in group) {
                    groupsToShow.add(group)
                }
                if (showLoadingUI) {
                    _dataLoading.value = false
                }
                isDataLoadingError.value = false

                val itemsValue = ArrayList(groupsToShow)
                _items.value = itemsValue
            }

            override fun onDataNotAvailable() {
                if (showLoadingUI) {
                    _dataLoading.value = false
                }
                isDataLoadingError.value = true
            }
        })*/
    }


    internal fun openGroup(groupName: String) {
        _openGroupEvent.value = Event(groupName)
    }

    class MergeGroupTask(
            private val groupMembersBillsDebtors: GroupMembersBillsDebtors,
            private val members: List<Member>,
            private val groupRepository: GroupRepository,
            private val billRepository: BillRepository,
            private val debtorRepository: DebtorRepository,
            private val memberRepository: MemberRepository,
            private val groupMemberRepository: GroupMemberRepository,
            private val snackbarText: MutableLiveData<Event<Int>>,
            private val groupMergedEvent: MutableLiveData<Event<Unit>>,
            private val groupAddedEvent: MutableLiveData<Event<Unit>>,
            private val groupMerging: MutableLiveData<Boolean>
    ) : AsyncTask<Unit, Unit, Int>() {

        override fun doInBackground(vararg params: Unit): Int {
            val group = groupMembersBillsDebtors.group
            val bills = groupMembersBillsDebtors.bills

            val oldGroup = groupRepository.getGroupSync(group.name)

            if (oldGroup != null) {

                members.forEach { member ->
                    val oldMember = memberRepository.getMemberByEmailSync(member.email)
                    if (oldMember == null) {
                        memberRepository.saveMemberSync(member)
                    }
                    groupMemberRepository.saveGroupMemberSync(GroupMember(group.name, member.email))
                }

                bills.forEach {
                    val bill = it.bill
                    val debtors = it.debtors
                    val oldBill = billRepository.getBillByIdSync(bill.id)
                    if (oldBill == null) {
                        billRepository.saveBillSync(bill)
                    } else {
                        if (!oldBill.valid) {
                            billRepository.updateBillSync(Bill(
                                    oldBill.dateTime,
                                    oldBill.description,
                                    oldBill.amount,
                                    oldBill.currency,
                                    oldBill.creditorEmail,
                                    oldBill.groupName,
                                    oldBill.valid))
                        } else {
                            billRepository.updateBillSync(Bill(
                                    oldBill.dateTime,
                                    oldBill.description,
                                    oldBill.amount,
                                    oldBill.currency,
                                    oldBill.creditorEmail,
                                    oldBill.groupName,
                                    bill.valid))
                        }
                    }
                    debtors.forEach { debtor ->
                        debtorRepository.saveDebtorSync(debtor)
                    }
                }
                return GROUP_MERGED
            }

            groupRepository.saveGroupSync(group)

            members.forEach { member ->
                memberRepository.saveMemberSync(member)
                groupMemberRepository.saveGroupMemberSync(GroupMember(group.name, member.email))
            }

            bills.forEach { billDebtors ->
                val bill = billDebtors.bill
                val debtors = billDebtors.debtors
                billRepository.saveBillSync(bill)
                debtors.forEach { debtor ->
                    debtorRepository.saveDebtorSync(debtor)
                }
            }

            return GROUP_ADDED
        }

        override fun onPostExecute(result: Int) {
            when (result) {
                GROUP_MERGED -> {
                    groupMerging.value = false
                    groupMergedEvent.value = Event(Unit)
                    snackbarText.value = Event(R.string.merged_group)
                }
                GROUP_ADDED -> {
                    groupMerging.value = false
                    groupAddedEvent.value = Event(Unit)
                    snackbarText.value = Event(R.string.added_group)
                }
            }
        }

        companion object {
            private val GROUP_MERGED = 1
            private val GROUP_ADDED = 2
            private val ERROR = 3
        }

    }

    companion object {
        private const val TAG = "GroupViewModel"
    }
}