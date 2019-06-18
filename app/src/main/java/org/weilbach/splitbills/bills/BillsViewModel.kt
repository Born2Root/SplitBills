package org.weilbach.splitbills.bills

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.view.MenuItem
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.weilbach.splitbills.Event
import org.weilbach.splitbills.R
import org.weilbach.splitbills.data2.Bill
import org.weilbach.splitbills.data2.Group
import org.weilbach.splitbills.data2.source.*
import org.weilbach.splitbills.util.getUser
import org.weilbach.splitbills.writeGroupToXml
import kotlin.math.absoluteValue

class BillsViewModel(
        val billsRepository: BillRepository,
        val membersRepository: MemberRepository,
        val debtorsRepository: DebtorRepository,
        val groupsMembersRepository: GroupMemberRepository,
        val groupRepository: GroupRepository,
        val appContext: Context
) : ViewModel() {

    private val _exportingGroup = MutableLiveData<Boolean>().apply {
        value = false
    }
    val exportingGroup: LiveData<Boolean>
        get() = _exportingGroup

    private val group = MutableLiveData<Group>()

    /*private var _items: LiveData<List<Bill>> = MutableLiveData<List<Bill>>().apply { value = emptyList() }
    val items: LiveData<List<Bill>>
        get() = _items*/

    val items: LiveData<List<Bill>> = Transformations.switchMap(group) {
        billsRepository.getBillsByGroupNameOrdered(it.name)
    }

    private val _dataLoading = MutableLiveData<Boolean>().apply {
        value = false
    }
    val dataLoading: LiveData<Boolean>
        get() = _dataLoading

    private val _newBillEvent = MutableLiveData<Event<Unit>>()
    val newBillEvent: LiveData<Event<Unit>>
        get() = _newBillEvent

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarMessage: LiveData<Event<Int>>
        get() = _snackbarText

    private val _openBillEvent = MutableLiveData<Event<String>>()
    val openBillEvent: LiveData<Event<String>>
        get() = _openBillEvent

    private val _openBalancesEvent = MutableLiveData<Event<String>>()
    val openBalancesEvent: LiveData<Event<String>>
        get() = _openBalancesEvent

    private val _shareGroupEvent = MutableLiveData<Event<GroupShare>>()
    val shareGroupEvent: LiveData<Event<GroupShare>>
        get() = _shareGroupEvent

    // private var groupName: String? = null

    val empty: LiveData<Boolean> = Transformations.map(items) {
        it.isEmpty()
    }

    fun openBalances() {
        group.value?.let { _openBalancesEvent.value = Event(it.name) }
    }

    fun start(groupName: String) {
        group.value = Group(groupName)
        // this.groupName = groupName
        //_items = billsRepository.getBillsByGroupNameOrdered(groupName)
        // loadBills(true)
        // loadBills(groupName, true)
    }

    /*fun start(groupName: String) {
        loadBills(groupName, true)
    }*/

    fun loadBills(forceUpdate: Boolean) {
        _dataLoading.value = false
     /*   groupName?.let {
            loadBills(it, forceUpdate)
            return
        }
        Log.w(TAG, "no group name set, can not load bills")*/
        // loadBills(forceUpdate, true)
    }

    fun loadBills(groupName: String, forceUpdate: Boolean) {
        /*Log.d(TAG, "load bills for group $groupName")
        this.groupName = groupName*/
        //loadBills(forceUpdate, true, groupName)
    }

    fun addNewBill() {
        _newBillEvent.value = Event(Unit)
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int) {
    }

    fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            0 -> {
                removeBill(item)
            }
        }
        return false
    }

    fun removeBill(item: MenuItem) {
        val pos = item.order
        // TODO: Present a warning dialog
        items.value?.let {
            if (pos >= it.size) {
                return@let
            }
            val bill = it[pos]

            billsRepository.updateBill(Bill(
                    bill.dateTime,
                    bill.description,
                    bill.amount,
                    bill.currency,
                    bill.creditorEmail,
                    bill.groupName,
                    false))
        }
/*        billRepository.saveBill(BillData(
                bill.dateTime,
                bill.description,
                bill.creditorEmail,
                bill.groupName,
                false))

        groupName?.let {
            loadBills(it, true)
        }*/
    }

    private fun loadBills(forceUpdate: Boolean, showLoadingUi: Boolean) {
        /*if (showLoadingUi) {
            _dataLoading.value = true
        }
        if (forceUpdate) {
            billsRepository.refresh()
        }

        _dataLoading.value = false*/

        /*
        billRepository.getBills(object : BillsDataSource.GetBillsCallback {
            override fun onBillsLoaded(bills: List<BillData>) {
                val billsToShow = ArrayList<BillData>()

                for (bill in bills) {
                    billsToShow.add(bill)
                }
                if (showLoadingUi) {
                    _dataLoading.value = false
                }

                val itemsValue = ArrayList(billsToShow)
                _items.value = itemsValue
            }

            override fun onDataNotAvailable() {
                if (showLoadingUi) {
                    _dataLoading.value = false
                }
            }
        })*/
    }

    private fun loadBills(forceUpdate: Boolean, showLoadingUi: Boolean, groupName: String) {
/*        if (showLoadingUi) {
            _dataLoading.value = true
        }
        if (forceUpdate) {
            billsRepository.refresh()
        }*/

        /*
        billsRepository.getBillsByGroupName(groupName, object : BillsDataSource.GetBillsCallback {
            override fun onBillsLoaded(bill: List<BillData>) {
                val billsToShow = ArrayList<BillData>()

                for (bill in bill) {
                    billsToShow.add(bill)
                }
                if (showLoadingUi) {
                    _dataLoading.value = false
                }

                val itemsValue = ArrayList(billsToShow).sortedWith(object : Comparator<BillData> {
                    override fun compare(o1: BillData, o2: BillData): Int {
                        val d1 = BillData.formatter.parse(o1.dateTime)
                        val d2 = BillData.formatter.parse(o2.dateTime)
                        val res = d1.compareTo(d2)
                        if (res > 0) return 0 - res
                        if (res < 0) return res.absoluteValue
                        return res
                    }
                })
                _items.value = itemsValue
            }

            override fun onDataNotAvailable() {
                if (showLoadingUi) {
                    _dataLoading.value = false
                }
            }
        })*/
    }

    private fun shareGroupViaMail() {
        group.value?.let {
            _exportingGroup.value = true
            ExportGroupTask(it.name,
                    groupRepository,
                    groupsMembersRepository,
                    _shareGroupEvent,
                    appContext,
                    _exportingGroup)
                    .execute()
        }
        //gro
/*        groupName?.let { groupName ->
            writeGroupToXml(
                    GroupData(groupName),
                    memberRepository,
                    billRepository,
                    groupMemberRepository,
                    debtorRepository,
                    amountsRepository,
                    AppExecutors(),
                    object : GroupWriterCallback {
                        override fun onSuccess(xml: String) {
                            groupMemberRepository.getGroupMembersByGroupName(groupName, object : GroupsMembersDataSource.GetGroupsMembersCallback {
                                override fun onGroupsMembersLoaded(groupsMemberData: List<GroupMemberData>) {
                                    val subject = "Updates in SplitBills from $groupName"
                                    val content = "Hi,\nthere were some updates in $groupName. To merge the" +
                                            " updates, open the file in the appendix with SplitBills."

                                    val emails = ArrayList<String>()
                                    groupsMemberData.forEach {
                                        emails.add(it.memberEmail)
                                    }
                                    _shareGroupEvent.value = Event(GroupShare(
                                            subject,
                                            content,
                                            xml,
                                            emails.toTypedArray()))
                                }

                                override fun onDataNotAvailable() {
                                }
                            })
                        }

                        override fun onFailure() {
                        }
                    })
        }*/
    }

    fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_frag_bills_share -> {
                shareGroupViaMail()
                return true
            }
        }
        return false
    }

    internal fun openBill(billId: String) {
        _openBillEvent.value = Event(billId)
    }

    companion object {
        private const val TAG = "BillsViewModel"
    }

    private class ExportGroupTask(private val groupName: String,
                                  private val groupRepository: GroupRepository,
                                  private val groupsMembersRepository: GroupMemberRepository,
                                  private val shareGroupEvent: MutableLiveData<Event<GroupShare>>,
                                  private val appContext: Context,
                                  private val exportingGroup: MutableLiveData<Boolean>
    ) : AsyncTask<String, Int, GroupShare?>() {

        override fun doInBackground(vararg params: String): GroupShare? {
            val user = getUser(appContext)
            val group = groupRepository.getGroupWithMembersAndBillsWithDebtorsByNameSync(groupName)
            val members = groupsMembersRepository.getGroupMembersMembersByGroupNameSync(groupName)
            val xml = writeGroupToXml(group, members)
            val subject = appContext.getString(R.string.email_subject, groupName)
            val content = appContext.getString(R.string.email_content, groupName)

            val addresses = members.filter { it.email != user.email }.map { member -> member.email }
            return GroupShare(subject, content, xml, addresses.toTypedArray())
        }

        override fun onPostExecute(result: GroupShare?) {
            exportingGroup.value = false
            result?.let {
                shareGroupEvent.value = Event(it)
            }
        }

    }
}