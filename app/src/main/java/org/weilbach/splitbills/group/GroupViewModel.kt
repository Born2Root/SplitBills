package org.weilbach.splitbills.group

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.view.MenuItem
import androidx.lifecycle.*
import org.weilbach.splitbills.*
import org.weilbach.splitbills.R
import org.weilbach.splitbills.addeditbill.AddEditBillActivity
import org.weilbach.splitbills.bills.GroupShare
import org.weilbach.splitbills.data.*
import org.weilbach.splitbills.data.source.*
import org.weilbach.splitbills.util.*
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.math.BigDecimal
import java.util.*

class GroupViewModel(val groupRepository: GroupRepository,
                     val memberRepository: MemberRepository,
                     val groupMemberRepository: GroupMemberRepository,
                     val billRepository: BillRepository,
                     val debtorRepository: DebtorRepository,
                     val appExecutors: AppExecutors,
                     val appContext: Context) : ViewModel() {

    private val _groupMerging = MutableLiveData<Boolean>().apply {
        value = false
    }
    val groupMerging: LiveData<Boolean>
        get() = _groupMerging

    private val user = getUserLive(appContext)
    private val currency = getCurrencyLive(appContext)

    private val items = groupRepository.getAll()

    val groupItemViewModels = Transformations.map(items) { groups ->
        groups?.map { group ->
            GroupItemViewModel(Group(group.name), this, appContext)
        }
    }

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

    private val _newBillEvent = MutableLiveData<Event<Unit>>()
    val newBillEvent: LiveData<Event<Unit>>
        get() = _newBillEvent

    private val _dataLoading = MutableLiveData<Boolean>().apply { value = false }
    val dataLoading: LiveData<Boolean>
        get() = _dataLoading

    private val _shareGroupEvent = MutableLiveData<Event<GroupShare>>()
    val shareGroupEvent: LiveData<Event<GroupShare>>
        get() = _shareGroupEvent

    private val _isGroupExporting = MutableLiveData<Boolean>().apply { value = false }
    val isGroupExporting: LiveData<Boolean>
        get() = _isGroupExporting

    private val groupsWithMembersAndBillsDebtors = groupRepository.getGroupsWithMembersAndBillsWithDebtors()

    private val totalOwe: LiveData<BigDecimal> = MediatorLiveData<BigDecimal>().apply {
        addSource(groupsWithMembersAndBillsDebtors) { groupsWithMembersAndBillsDebtors ->
            user.value?.let { user ->
                value = calcTotalOwe(user, groupsWithMembersAndBillsDebtors)
            }
        }
        addSource(user) { user ->
            groupsWithMembersAndBillsDebtors.value?.let { groupsWithMembersAndBillsDebtors ->
                value = calcTotalOwe(user, groupsWithMembersAndBillsDebtors)
            }
        }
    }

    private fun calcTotalOwe(user: Member, groupsWithMembersAndBillsDebtors: List<GroupMembersBillsDebtors>): BigDecimal {
        var res = BigDecimal.ZERO
        groupsWithMembersAndBillsDebtors.forEach { groupWithMembersAndBillsDebtors ->
            res = res.add(memberOwesGroupTotal(user, groupWithMembersAndBillsDebtors))
        }
        return res
    }

    private val totalGet: LiveData<BigDecimal> = MediatorLiveData<BigDecimal>().apply {
        addSource(groupsWithMembersAndBillsDebtors) { groupsWithMembersAndBillsDebtors ->
            user.value?.let { user ->
                value = calcTotalGet(user, groupsWithMembersAndBillsDebtors)
            }
        }
        addSource(user) { user ->
            groupsWithMembersAndBillsDebtors.value?.let { groupsWithMembersAndBillsDebtors ->
                value = calcTotalGet(user, groupsWithMembersAndBillsDebtors)
            }
        }
    }

    private fun calcTotalGet(user: Member, groupsWithMembersAndBillsDebtors: List<GroupMembersBillsDebtors>): BigDecimal {
        var res = BigDecimal.ZERO
        groupsWithMembersAndBillsDebtors.forEach { groupWithMembersAndBillsDebtors ->
            res = res.add(memberGetsFromGroupTotal(user, groupWithMembersAndBillsDebtors))
        }
        return res
    }

    private val _oweGetTotal = MediatorLiveData<String>().apply {
        addSource(totalOwe) { owe ->
            val get = totalGet.value
            val curr = currency.value
            if (curr != null && get != null) {
                value = calcOweGetTotal(owe, get, Currency.getInstance(curr))
            }
        }
        addSource(totalGet) { get ->
            val owe = totalOwe.value
            val curr = currency.value
            if (curr != null && owe != null) {
                value = calcOweGetTotal(owe, get, Currency.getInstance(curr))
            }
        }
        addSource(currency) { curr ->
            val owe = totalOwe.value
            val get = totalGet.value
            if (get != null && owe != null) {
                value = calcOweGetTotal(owe, get, Currency.getInstance(curr))
            }
        }
    }
    val oweGetTotal: LiveData<String>
        get() = _oweGetTotal

    private fun calcOweGetTotal(owe: BigDecimal, get: BigDecimal, currency: Currency): String {
        val res = get.subtract(owe)

        return when (res.compareTo(BigDecimal.ZERO)) {
            1 -> {
                _totalBalanceColor.value = R.color.colorGet
                appContext.getString(R.string.you_get, prettyPrintNum(res), currency.symbol)
            }
            -1 -> {
                _totalBalanceColor.value = R.color.colorOwe
                appContext.getString(R.string.you_owe, prettyPrintNum(res.abs()), currency.symbol)
            }
            else -> {
                _totalBalanceColor.value = R.color.colorGet
                appContext.getString(R.string.you_are_settled_up)
            }
        }
    }

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

    fun loadGroups(forceUpdate: Boolean) {
        _dataLoading.value = false
    }

    fun addNewGroup() {
        _newGroupEvent.value = Event(Unit)
    }

    fun shareGroup(groupName: String) {
        _isGroupExporting.value = true

        ExportGroupTask(groupName, groupRepository, groupMemberRepository, appContext) { result ->
            _isGroupExporting.value = false
            result?.let { groupShare ->
                _shareGroupEvent.value = Event(groupShare)
            }
        }.execute()
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        when (requestCode) {
            AddEditBillActivity.REQUEST_CODE -> {
                if (resultCode == ADD_EDIT_RESULT_OK) {
                    _snackbarText.value = Event(R.string.bill_saved)
                }
            }
            GroupActivity.IMPORT_GROUP_REQUEST -> {
                if (resultCode == RESULT_OK) {
                    intent?.let {
                        it.data?.let { uri ->
                            importGroup(uri)
                        }
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

        items.value?.let {
            if (pos >= it.size) {
                return@let
            }
            val group = it[pos]

            groupRepository.deleteGroup(group.name)
        }
    }

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
        // Not handle intent twice, may not be optimal solution
        intent.action = null
    }

    private fun importGroup(uri: Uri) {
        _snackbarText.value = Event(R.string.trying_to_merge)
        _groupMerging.value = true
        _groupMergeStartedEvent.value = Event(Unit)
        val inputStream = appContext.contentResolver.openInputStream(uri)

        ReadGroupTask(inputStream, _groupMerging, _groupMergeFailed, _snackbarText) { res ->
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

        }.execute()
    }

    internal fun openGroup(groupName: String) {
        _openGroupEvent.value = Event(groupName)
    }

    class ReadGroupTask(
            private val inputStream: InputStream?,
            private val groupMerging: MutableLiveData<Boolean>,
            private val groupMergeFailed: MutableLiveData<Event<Unit>>,
            private val snackbarText: MutableLiveData<Event<Int>>,
            private val success: (Triple<GroupMembersBillsDebtors, List<Member>, String>) -> Unit
    ) : AsyncTask<Unit, Unit, Pair<Int, Triple<GroupMembersBillsDebtors, List<Member>, String>?>>() {

        override fun doInBackground(vararg params: Unit?): Pair<Int, Triple<GroupMembersBillsDebtors, List<Member>, String>?> {
            if (inputStream != null) {
                var res: Triple<GroupMembersBillsDebtors, List<Member>, String>? = null

                try {
                    res = readGroupFromXml(inputStream)
                } catch (e: IOException) {
                    return Pair(IO_ERROR, null)
                } catch (e: XmlPullParserException) {
                    return Pair(NO_VALID_XML_FILE, null)
                }

                if (res.third != "1") {
                    return Pair(DATABASE_VERSION_ERROR, null)
                }

                return Pair(SUCCESS, res)
            }
            return Pair(IO_ERROR, null)
        }

        override fun onPostExecute(result: Pair<Int, Triple<GroupMembersBillsDebtors, List<Member>, String>?>) {

            when (result.first) {
                NO_VALID_XML_FILE -> {
                    snackbarText.value = Event(R.string.could_not_merge_no_group)
                    groupMerging.value = false
                    groupMergeFailed.value = Event(Unit)
                }
                IO_ERROR -> {
                    snackbarText.value = Event(R.string.could_not_merge_group_io_error)
                    groupMerging.value = false
                    groupMergeFailed.value = Event(Unit)
                }
                DATABASE_VERSION_ERROR -> {
                    snackbarText.value = Event(R.string.database_error)
                    groupMerging.value = false
                    groupMergeFailed.value = Event(Unit)
                }
                SUCCESS -> {
                    // Should never be null
                    result.second?.let {
                        success(it)
                    }
                }
            }
        }

        companion object {
            private const val NO_VALID_XML_FILE = 1
            private const val IO_ERROR = 2
            private const val SUCCESS = 3
            private const val DATABASE_VERSION_ERROR = 4
        }
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
                val oldMember = memberRepository.getMemberByEmailSync(member.email)
                if (oldMember == null) {
                    memberRepository.saveMemberSync(member)
                }
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