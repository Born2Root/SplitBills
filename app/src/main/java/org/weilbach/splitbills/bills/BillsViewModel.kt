package org.weilbach.splitbills.bills

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.weilbach.splitbills.Event
import org.weilbach.splitbills.R
import org.weilbach.splitbills.data.Bill
import org.weilbach.splitbills.data.source.BillsDataSource
import org.weilbach.splitbills.data.source.BillsRepository

class BillsViewModel(private val billsRepository: BillsRepository) : ViewModel() {

    private val _items = MutableLiveData<List<Bill>>().apply { value = emptyList() }
    val items: LiveData<List<Bill>>
        get() = _items

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean>
        get() = _dataLoading

    private val _noBillsLabel = MutableLiveData<Int>()
    val noBillsLabel: LiveData<Int>
        get() = _noBillsLabel

    private val _noBillsIconRes = MutableLiveData<Int>()
    val noBillsIconRes: LiveData<Int>
        get() = _noBillsIconRes

    private val _newBillEvent = MutableLiveData<Event<Unit>>()
    val newBillEvent: LiveData<Event<Unit>>
        get() = _newBillEvent

    private val _billsAddViewVisible = MutableLiveData<Boolean>()
    val billsAddViewVisible: LiveData<Boolean>
        get() = _billsAddViewVisible

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarMessage: LiveData<Event<Int>>
        get() = _snackbarText

    private val _openBillEvent = MutableLiveData<Event<String>>()
    val openBillEvent: LiveData<Event<String>>
        get() = _openBillEvent

    init {
        _dataLoading.value = true
        _noBillsLabel.value = R.string.no_bills_all
        _billsAddViewVisible.value = true
        _noBillsIconRes.value = R.drawable.ic_assignment_turned_in_24dp
    }

    val empty: LiveData<Boolean> = Transformations.map(_items) {
        it.isEmpty()
    }

    fun start() {
        loadBills(false)
    }

    fun loadBills(forceUpdate: Boolean) {
        loadBills(forceUpdate, true)
    }

    fun addNewBill() {
        _newBillEvent.value = Event(Unit)
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int) {
    }

    private fun loadBills(forceUpdate: Boolean, showLoadingUi: Boolean) {
        if (showLoadingUi) {
            _dataLoading.value = true
        }
        if (forceUpdate) {
            billsRepository.refreshBills()
        }

        billsRepository.getBills(object : BillsDataSource.GetBillsCallback {
            override fun onBillsLoaded(bills: List<Bill>) {
                val billsToShow = ArrayList<Bill>()

                for (bill in bills) {
                    billsToShow.add(bill)
                }
                if (showLoadingUi) {
                    _dataLoading.value = false
                }

                val itemsValue = ArrayList(billsToShow)
            }

            override fun onDataNotAvailable() {
                if (showLoadingUi) {
                    _dataLoading.value = false
                }
            }
        })
    }

    internal fun openBill(billId: String) {
        _openBillEvent.value = Event(billId)
    }
}