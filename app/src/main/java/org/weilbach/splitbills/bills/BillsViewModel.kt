package org.weilbach.splitbills.bills

import android.content.Context
import android.os.AsyncTask
import android.view.MenuItem
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.weilbach.splitbills.ExportGroupTask
import org.weilbach.splitbills.util.Event
import org.weilbach.splitbills.R
import org.weilbach.splitbills.data.Bill
import org.weilbach.splitbills.data.Group
import org.weilbach.splitbills.data.source.*
import org.weilbach.splitbills.util.getUser
import org.weilbach.splitbills.writeGroupToXml

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

    fun openBalances() {
        group.value?.let { _openBalancesEvent.value = Event(it.name) }
    }

    fun start(groupName: String) {
        group.value = Group(groupName)
    }

    fun loadBills(forceUpdate: Boolean) {
        _dataLoading.value = false
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int) {}

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
    }

    private fun shareGroupViaMail() {
        group.value?.let { group ->
            _exportingGroup.value = true

            ExportGroupTask(group.name, groupRepository, groupsMembersRepository, appContext) { result ->
                _exportingGroup.value = false
                result?.let { groupShare ->
                    _shareGroupEvent.value = Event(groupShare)
                }
            }.execute()
        }
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
}