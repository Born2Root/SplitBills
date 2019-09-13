package org.weilbach.splitbills.addeditbill

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import org.weilbach.splitbills.prettyPrintNum
import org.weilbach.splitbills.util.Event
import java.math.BigDecimal
import java.util.*

class DebtorItemViewModel(
        val name: String,
        val email: String
) {

    val splitMode = MutableLiveData<AddEditBillViewModel.SplitMode>()

    val amount = MutableLiveData<BigDecimal>()

    val currency = MutableLiveData<Currency>()

    val _amountPretty = MediatorLiveData<String>().apply {
        addSource(splitMode) { splitMode ->
            val currentAmount = amount.value ?: return@addSource
            val currentCurrency = currency.value ?: return@addSource
            value = calcAmountPretty(splitMode, currentAmount, currentCurrency)
        }

        addSource(amount) { amount ->
            val currentSplitMode = splitMode.value ?: return@addSource
            val currentCurrency = currency.value ?: return@addSource
            value = calcAmountPretty(currentSplitMode, amount, currentCurrency)
        }

        addSource(currency) { currency ->
            val currentSplitMode = splitMode.value ?: return@addSource
            val currentAmount = amount.value ?: return@addSource
            value = calcAmountPretty(currentSplitMode, currentAmount, currency)
        }
    }
    val amountPretty: LiveData<String>
        get() = _amountPretty

    private fun calcAmountPretty(splitMode: AddEditBillViewModel.SplitMode, amount: BigDecimal, currency: Currency): String {
        return when (splitMode) {
            AddEditBillViewModel.SplitMode.ABSOLUTE -> {
                prettyPrintNum(amount) + " " + currency.symbol
            }
            AddEditBillViewModel.SplitMode.PERCENTAGE -> {
                prettyPrintNum(amount) + " %"
            }
        }
    }

    val isAmountValid = MutableLiveData<Boolean>().apply {
        value = false
    }

    private val _changeAmountEvent = MutableLiveData<Event<DebtorItemViewModel>>()
    val changeAmountEvent: LiveData<Event<DebtorItemViewModel>>
        get() = _changeAmountEvent

    private val _removeEvent = MutableLiveData<Event<DebtorItemViewModel>>()
    val removeEvent: LiveData<Event<DebtorItemViewModel>>
        get() = _removeEvent

    fun remove() {
        _removeEvent.value = Event(this)
    }

    fun changeAmount() {
        _changeAmountEvent.value = Event(this)
    }
}